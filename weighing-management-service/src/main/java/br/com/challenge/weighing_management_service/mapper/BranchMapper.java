package br.com.challenge.weighing_management_service.mapper;

import br.com.challenge.weighing_management_service.dto.BranchRequestDto;
import br.com.challenge.weighing_management_service.dto.BranchResponseDto;
import br.com.challenge.weighing_management_service.entity.Branch;
import org.springframework.stereotype.Component;

@Component
public class BranchMapper {

    public Branch toEntity(BranchRequestDto dto) {
        return Branch.builder()
                .name(dto.getName())
                .city(dto.getCity())
                .state(dto.getState())
                .address(dto.getAddress())
                .build();
    }

    public BranchResponseDto toResponseDto(Branch entity) {
        return BranchResponseDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .city(entity.getCity())
                .state(entity.getState())
                .address(entity.getAddress())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public void updateEntityFromDto(BranchRequestDto dto, Branch entity) {
        entity.setName(dto.getName());
        entity.setCity(dto.getCity());
        entity.setState(dto.getState());
        entity.setAddress(dto.getAddress());
    }
}