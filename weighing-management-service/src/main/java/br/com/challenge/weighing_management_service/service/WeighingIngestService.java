package br.com.challenge.weighing_management_service.service;

import br.com.challenge.weighing_management_service.dto.WeighingIngestDto;
import br.com.challenge.weighing_management_service.entity.*;
import br.com.challenge.weighing_management_service.exception.BusinessException;
import br.com.challenge.weighing_management_service.exception.ResourceNotFoundException;
import br.com.challenge.weighing_management_service.repository.ScaleRepository;
import br.com.challenge.weighing_management_service.repository.TruckRepository;
import br.com.challenge.weighing_management_service.repository.WeighingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeighingIngestService {

    private final WeighingRepository weighingRepository;
    private final TruckRepository truckRepository;
    private final ScaleRepository scaleRepository;
    private final TransportTransactionService transactionService;

    @Transactional
    public Weighing processWeighing(WeighingIngestDto ingestDto) {
        log.info("Processing weighing for plate: {} with weight: {}", ingestDto.getPlate(), ingestDto.getGrossWeight());

        Truck truck = truckRepository.findByPlate(ingestDto.getPlate())
                .orElseThrow(() -> new ResourceNotFoundException("Truck not found with plate: " + ingestDto.getPlate()));

        Scale scale = scaleRepository.findByCode(ingestDto.getScaleId().trim())
                .orElseThrow(() -> new ResourceNotFoundException("Scale not found with code: " + ingestDto.getScaleId()));

        if (!scale.getActive()) {
            throw new BusinessException("Scale is not active: " + scale.getCode());
        }

        Optional<TransportTransaction> transactionOpt = transactionService.findInProgressTransactionByTruck(truck.getId());

        if (transactionOpt.isEmpty()) {
            throw new BusinessException("No active transport transaction found for truck: " + truck.getPlate());
        }

        TransportTransaction transaction = transactionOpt.get();

        BigDecimal tareWeight = truck.getTareWeight();
        BigDecimal grossWeight = ingestDto.getGrossWeight();
        BigDecimal netWeight = grossWeight.subtract(tareWeight);

        if (netWeight.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Net weight must be greater than zero. Gross: " + grossWeight + ", Tare: " + tareWeight);
        }

        BigDecimal weightInTons = netWeight.divide(new BigDecimal("1000"), 2, BigDecimal.ROUND_HALF_UP);
        BigDecimal pricePerTon = transaction.getGrainType().getPurchasePricePerTon();
        BigDecimal loadCost = weightInTons.multiply(pricePerTon);

        Weighing weighing = Weighing.builder()
                .transaction(transaction)
                .scale(scale)
                .grossWeight(grossWeight)
                .tareWeight(tareWeight)
                .netWeight(netWeight)
                .loadCost(loadCost)
                .weighingTime(LocalDateTime.now())
                .build();

        Weighing savedWeighing = weighingRepository.save(weighing);

        log.info("Weighing processed successfully. ID: {}, Net Weight: {} kg, Load Cost: R$ {}",
                savedWeighing.getId(), netWeight, loadCost);

        return savedWeighing;
    }
}
