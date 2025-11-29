package br.com.challenge.weighing_management_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportRequestDto {

    private Long branchId;
    private Long truckId;
    private Long grainTypeId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}