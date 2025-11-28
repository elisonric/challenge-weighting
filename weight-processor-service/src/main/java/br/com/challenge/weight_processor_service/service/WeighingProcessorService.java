package br.com.challenge.weight_processor_service.service;

import br.com.challenge.weight_processor_service.core.KeyAssignmentRegistry;
import br.com.challenge.weight_processor_service.core.WorkerInfo;
import br.com.challenge.weight_processor_service.dto.WeighingEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeighingProcessorService {

    private static final int MIN_SAMPLES = 30;
    private static final int WINDOW_SIZE = 20;
    private static final BigDecimal MAX_VARIATION = new BigDecimal("0.0003");
    private final Map<String, List<BigDecimal>> plateWeights = new ConcurrentHashMap<>();
    private static final MathContext MC = new MathContext(10, RoundingMode.HALF_UP);

    private final WorkerInfo workerInfo;

    public boolean process(WeighingEventDto weighingEventDto) {
        String plate = weighingEventDto.getPlate();
        BigDecimal weight = weighingEventDto.getWeight();

        try {
            log.info("Worker {} PROCESSANDO plate {} eventId {}",
                    workerInfo.getWorkerId(), weighingEventDto.getPlate(), weighingEventDto.getId());

            plateWeights.computeIfAbsent(plate, p -> new ArrayList<>()).add(weight);

            List<BigDecimal> list = plateWeights.get(plate);

            if (list.size() < MIN_SAMPLES) {
                return false;
            }

            List<BigDecimal> window =
                    list.subList(Math.max(0, list.size() - WINDOW_SIZE), list.size());

            BigDecimal max = window.stream().max(BigDecimal::compareTo).orElse(weight);
            BigDecimal min = window.stream().min(BigDecimal::compareTo).orElse(weight);

            BigDecimal sum = window.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal avg = sum.divide(new BigDecimal(window.size()), MC);

            BigDecimal diff = max.subtract(min);
            BigDecimal variation = diff.divide(avg, MC);

            if (variation.compareTo(MAX_VARIATION) <= 0) {

                log.info("\n=== PESO ESTÁVEL DETECTADO ===");
                log.info("Placa: " + plate);
                log.info("Peso final: " + avg);
                log.info("Variação: " + variation + "\n");

                plateWeights.remove(plate);
                return true;
            }

            log.info("Worker {} Register plate {} eventId {}",
                    workerInfo.getWorkerId(), weighingEventDto.getPlate(), weighingEventDto.getId());
            return false;
        } catch (Exception ex) {
            log.error("Erro ao processar plate {} pelo worker {}", weighingEventDto.getPlate(), workerInfo.getWorkerId(), ex);
            return false;
        }
    }
}
