package br.com.challenge.weighing_management_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScaleRequestDto {

    @NotBlank(message = "Code is required")
    private String code;

    private String location;

    @NotNull(message = "Branch ID is required")
    private Long branchId;

    private Boolean active;
}
