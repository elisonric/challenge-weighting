package br.com.challenge.weighing_management_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeighingIngestDto {

    @NotBlank(message = "Plate is required")
    private String plate;

    @NotNull(message = "Gross weight is required")
    private BigDecimal grossWeight;

    @NotNull(message = "Scale ID is required")
    private String scaleId;
}
