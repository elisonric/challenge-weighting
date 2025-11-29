package br.com.challenge.weighing_management_service.service;

import br.com.challenge.weighing_management_service.dto.CostReportDto;
import br.com.challenge.weighing_management_service.dto.ProfitReportDto;
import br.com.challenge.weighing_management_service.dto.WeighingReportDto;
import br.com.challenge.weighing_management_service.entity.Weighing;
import br.com.challenge.weighing_management_service.repository.WeighingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {

    private final WeighingRepository weighingRepository;

    private static final BigDecimal MIN_PROFIT_MARGIN = new BigDecimal("0.05");
    private static final BigDecimal MAX_PROFIT_MARGIN = new BigDecimal("0.20");

    @Transactional(readOnly = true)
    public List<WeighingReportDto> getWeighings(Long branchId, Long truckId, Long grainTypeId,
                                                  LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Fetching weighing reports with filters");

        List<Weighing> weighings = filterWeighings(branchId, truckId, grainTypeId, startDate, endDate);

        return weighings.stream()
                .map(this::mapToWeighingReportDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CostReportDto> getCosts(String groupBy, Long branchId, Long truckId, Long grainTypeId,
                                         LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Generating cost report grouped by: {}", groupBy);

        if (groupBy == null || groupBy.isEmpty()) {
            throw new IllegalArgumentException("groupBy parameter is required. Valid values: branch, truck, grainType");
        }

        List<Weighing> weighings = filterWeighings(branchId, truckId, grainTypeId, startDate, endDate);

        return switch (groupBy.toLowerCase()) {
            case "branch" -> groupCostByBranch(weighings);
            case "truck" -> groupCostByTruck(weighings);
            case "graintype" -> groupCostByGrainType(weighings);
            default -> throw new IllegalArgumentException("Invalid groupBy value: " + groupBy + ". Valid values: branch, truck, grainType");
        };
    }

    @Transactional(readOnly = true)
    public List<ProfitReportDto> getProfits(String groupBy, Long branchId, Long truckId, Long grainTypeId,
                                             LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Generating profit report grouped by: {}", groupBy);

        if (groupBy == null || groupBy.isEmpty()) {
            throw new IllegalArgumentException("groupBy parameter is required. Valid values: branch, truck, grainType");
        }

        List<Weighing> weighings = filterWeighings(branchId, truckId, grainTypeId, startDate, endDate);

        return switch (groupBy.toLowerCase()) {
            case "branch" -> groupProfitByBranch(weighings);
            case "truck" -> groupProfitByTruck(weighings);
            case "graintype" -> groupProfitByGrainType(weighings);
            default -> throw new IllegalArgumentException("Invalid groupBy value: " + groupBy + ". Valid values: branch, truck, grainType");
        };
    }

    private List<Weighing> filterWeighings(Long branchId, Long truckId, Long grainTypeId,
                                           LocalDateTime startDate, LocalDateTime endDate) {

        if (startDate != null && endDate != null) {
            if (branchId != null) {
                return weighingRepository.findByBranchAndPeriod(branchId, startDate, endDate);
            } else if (truckId != null) {
                return weighingRepository.findByTruckAndPeriod(truckId, startDate, endDate);
            } else if (grainTypeId != null) {
                return weighingRepository.findByGrainTypeAndPeriod(grainTypeId, startDate, endDate);
            } else {
                return weighingRepository.findByPeriod(startDate, endDate);
            }
        } else if (branchId != null) {
            return weighingRepository.findByBranchId(branchId);
        } else if (truckId != null) {
            return weighingRepository.findByTruckId(truckId);
        } else if (grainTypeId != null) {
            return weighingRepository.findByGrainTypeId(grainTypeId);
        } else {
            return weighingRepository.findAll();
        }
    }

    private WeighingReportDto mapToWeighingReportDto(Weighing weighing) {
        return WeighingReportDto.builder()
                .weighingId(weighing.getId())
                .truckPlate(weighing.getTransaction().getTruck().getPlate())
                .branchName(weighing.getTransaction().getBranch().getName())
                .grainType(weighing.getTransaction().getGrainType().getName())
                .scaleCode(weighing.getScale().getCode())
                .grossWeight(weighing.getGrossWeight())
                .tareWeight(weighing.getTareWeight())
                .netWeight(weighing.getNetWeight())
                .loadCost(weighing.getLoadCost())
                .weighingTime(weighing.getWeighingTime())
                .build();
    }

    private List<CostReportDto> groupCostByBranch(List<Weighing> weighings) {
        Map<String, List<Weighing>> grouped = weighings.stream()
                .collect(Collectors.groupingBy(w -> w.getTransaction().getBranch().getName()));

        return grouped.entrySet().stream()
                .map(entry -> buildCostReport("branch", entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    private List<CostReportDto> groupCostByTruck(List<Weighing> weighings) {
        Map<String, List<Weighing>> grouped = weighings.stream()
                .collect(Collectors.groupingBy(w -> w.getTransaction().getTruck().getPlate()));

        return grouped.entrySet().stream()
                .map(entry -> buildCostReport("truck", entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    private List<CostReportDto> groupCostByGrainType(List<Weighing> weighings) {
        Map<String, List<Weighing>> grouped = weighings.stream()
                .collect(Collectors.groupingBy(w -> w.getTransaction().getGrainType().getName()));

        return grouped.entrySet().stream()
                .map(entry -> buildCostReport("grainType", entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    private CostReportDto buildCostReport(String groupBy, String groupName, List<Weighing> weighings) {
        BigDecimal totalCost = weighings.stream()
                .map(Weighing::getLoadCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalWeight = weighings.stream()
                .map(Weighing::getNetWeight)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CostReportDto.builder()
                .groupBy(groupBy)
                .groupName(groupName)
                .totalCost(totalCost)
                .totalWeight(totalWeight)
                .weighingCount((long) weighings.size())
                .build();
    }

    private List<ProfitReportDto> groupProfitByBranch(List<Weighing> weighings) {
        Map<String, List<Weighing>> grouped = weighings.stream()
                .collect(Collectors.groupingBy(w -> w.getTransaction().getBranch().getName()));

        List<ProfitReportDto> result = new ArrayList<>();
        for (Map.Entry<String, List<Weighing>> entry : grouped.entrySet()) {
            result.addAll(buildProfitReports("branch", entry.getKey(), entry.getValue()));
        }
        return result;
    }

    private List<ProfitReportDto> groupProfitByTruck(List<Weighing> weighings) {
        Map<String, List<Weighing>> grouped = weighings.stream()
                .collect(Collectors.groupingBy(w -> w.getTransaction().getTruck().getPlate()));

        List<ProfitReportDto> result = new ArrayList<>();
        for (Map.Entry<String, List<Weighing>> entry : grouped.entrySet()) {
            result.addAll(buildProfitReports("truck", entry.getKey(), entry.getValue()));
        }
        return result;
    }

    private List<ProfitReportDto> groupProfitByGrainType(List<Weighing> weighings) {
        Map<String, List<Weighing>> grouped = weighings.stream()
                .collect(Collectors.groupingBy(w -> w.getTransaction().getGrainType().getName()));

        List<ProfitReportDto> result = new ArrayList<>();
        for (Map.Entry<String, List<Weighing>> entry : grouped.entrySet()) {
            result.addAll(buildProfitReports("grainType", entry.getKey(), entry.getValue()));
        }
        return result;
    }

    private List<ProfitReportDto> buildProfitReports(String groupBy, String groupName, List<Weighing> weighings) {
        Map<String, List<Weighing>> byGrainType = weighings.stream()
                .collect(Collectors.groupingBy(w -> w.getTransaction().getGrainType().getName()));

        List<ProfitReportDto> reports = new ArrayList<>();

        for (Map.Entry<String, List<Weighing>> entry : byGrainType.entrySet()) {
            String grainTypeName = entry.getKey();
            List<Weighing> grainWeighings = entry.getValue();

            BigDecimal totalWeight = grainWeighings.stream()
                    .map(Weighing::getNetWeight)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal purchaseCost = grainWeighings.stream()
                    .map(Weighing::getLoadCost)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal minSalePrice = purchaseCost.multiply(BigDecimal.ONE.add(MIN_PROFIT_MARGIN))
                    .setScale(2, RoundingMode.HALF_UP);
            BigDecimal maxSalePrice = purchaseCost.multiply(BigDecimal.ONE.add(MAX_PROFIT_MARGIN))
                    .setScale(2, RoundingMode.HALF_UP);

            BigDecimal minProfit = minSalePrice.subtract(purchaseCost);
            BigDecimal maxProfit = maxSalePrice.subtract(purchaseCost);

            ProfitReportDto report = ProfitReportDto.builder()
                    .groupBy(groupBy)
                    .groupName(groupName)
                    .grainType(grainTypeName)
                    .totalWeight(totalWeight)
                    .purchaseCost(purchaseCost)
                    .minProfitMargin(MIN_PROFIT_MARGIN)
                    .maxProfitMargin(MAX_PROFIT_MARGIN)
                    .minSalePrice(minSalePrice)
                    .maxSalePrice(maxSalePrice)
                    .minProfit(minProfit)
                    .maxProfit(maxProfit)
                    .availableStock((long) grainWeighings.size())
                    .build();

            reports.add(report);
        }

        return reports;
    }
}