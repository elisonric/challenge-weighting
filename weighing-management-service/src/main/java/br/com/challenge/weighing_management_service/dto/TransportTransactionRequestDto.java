package br.com.challenge.weighing_management_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransportTransactionRequestDto {

    @NotNull(message = "Truck ID is required")
    private Long truckId;

    @NotNull(message = "Grain type ID is required")
    private Long grainTypeId;

    @NotNull(message = "Branch ID is required")
    private Long branchId;

    private LocalDateTime startTime;
}
