package br.com.challenge.weight_processor_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeighingOutputEventDto {

    private String plate;
    private BigDecimal grossWeight;
    private String scaleId;
}
