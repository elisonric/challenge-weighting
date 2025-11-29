package br.com.challenge.weighing_management_service.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TruckRequestDto {

    @NotBlank(message = "Plate is required")
    @Size(max = 10, message = "Plate must not exceed 10 characters")
    @Pattern(regexp = "^[A-Z]{3}-[0-9]{4}$|^[A-Z]{3}[0-9][A-Z][0-9]{2}$",
            message = "Invalid plate format. Use ABC-1234 or ABC1D23")
    private String plate;

    @Size(max = 50, message = "Model must not exceed 50 characters")
    private String model;

    @NotNull(message = "Tare weight is required")
    @DecimalMin(value = "0.01", message = "Tare weight must be greater than 0")
    private BigDecimal tareWeight;

    @NotNull(message = "Branch ID is required")
    private Long branchId;
}