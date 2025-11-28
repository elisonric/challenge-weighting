package br.com.challenge.weighing_ingest_service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

import static java.lang.System.*;

@AllArgsConstructor
@Getter
public class WeighingIngestInputDto {

    @NotBlank(message = "id is required")
    private String id;

    @NotBlank(message = "plate is required")
    @Pattern(
            regexp = "^[A-Z]{3}-?\\d[A-Z0-9]\\d{2}$",
            message = "invalid plate format"
    )
    private String plate;

    @NotNull(message = "weight is required")
    @DecimalMin(value = "0.01", message = "weight must be greater than zero")
    private BigDecimal weight;

    @JsonIgnore
    private Long timestamp = currentTimeMillis();
}
