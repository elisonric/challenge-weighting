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
public class TruckResponseDto {

    private Long id;
    private String plate;
    private String model;
    private BigDecimal tareWeight;
    private BranchResponseDto branch;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}