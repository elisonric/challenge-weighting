package br.com.challenge.weight_processor_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class WeighingEventDto {

    private String id;
    private String plate;
    private BigDecimal weight;
}
