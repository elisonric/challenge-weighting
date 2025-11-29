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
public class ScaleResponseDto {

    private Long id;
    private String code;
    private String location;
    private BranchResponseDto branch;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
