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
public class CostReportDto {

    private String groupBy;
    private String groupName;
    private BigDecimal totalCost;
    private BigDecimal totalWeight;
    private Long weighingCount;
}