package br.com.challenge.weighing_management_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfitReportDto {

    private String groupBy;
    private String groupName;
    private String grainType;
    private BigDecimal totalWeight;
    private BigDecimal purchaseCost;
    private BigDecimal minProfitMargin;
    private BigDecimal maxProfitMargin;
    private BigDecimal minSalePrice;
    private BigDecimal maxSalePrice;
    private BigDecimal minProfit;
    private BigDecimal maxProfit;
    private Long availableStock;
}