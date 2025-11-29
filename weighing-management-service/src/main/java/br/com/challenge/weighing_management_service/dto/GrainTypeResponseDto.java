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
public class GrainTypeResponseDto {

    private Long id;
    private String name;
    private String description;
    private BigDecimal purchasePricePerTon;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}