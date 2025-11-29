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
public class BranchResponseDto {

    private Long id;
    private String name;
    private String city;
    private String state;
    private String address;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}