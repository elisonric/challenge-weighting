package br.com.challenge.weighing_management_service.mapper;

import br.com.challenge.weighing_management_service.dto.GrainTypeRequestDto;
import br.com.challenge.weighing_management_service.dto.GrainTypeResponseDto;
import br.com.challenge.weighing_management_service.entity.GrainType;
import org.springframework.stereotype.Component;

@Component
public class GrainTypeMapper {

    public GrainType toEntity(GrainTypeRequestDto dto) {
        return GrainType.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .purchasePricePerTon(dto.getPurchasePricePerTon())
                .build();
    }

    public GrainTypeResponseDto toResponseDto(GrainType entity) {
        return GrainTypeResponseDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .purchasePricePerTon(entity.getPurchasePricePerTon())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public void updateEntityFromDto(GrainTypeRequestDto dto, GrainType entity) {
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setPurchasePricePerTon(dto.getPurchasePricePerTon());
    }
}