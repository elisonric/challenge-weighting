package br.com.challenge.weighing_management_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeighingReportDto {

    private Long weighingId;
    private String truckPlate;
    private String branchName;
    private String grainType;
    private String scaleCode;
    private BigDecimal grossWeight;
    private BigDecimal tareWeight;
    private BigDecimal netWeight;
    private BigDecimal loadCost;
    private LocalDateTime weighingTime;
}