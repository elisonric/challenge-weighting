package br.com.challenge.weighing_management_service.mapper;

import br.com.challenge.weighing_management_service.dto.ScaleRequestDto;
import br.com.challenge.weighing_management_service.dto.ScaleResponseDto;
import br.com.challenge.weighing_management_service.entity.Branch;
import br.com.challenge.weighing_management_service.entity.Scale;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScaleMapper {

    private final BranchMapper branchMapper;

    public Scale toEntity(ScaleRequestDto dto, Branch branch) {
        return Scale.builder()
                .code(dto.getCode())
                .location(dto.getLocation())
                .branch(branch)
                .active(dto.getActive() != null ? dto.getActive() : true)
                .build();
    }

    public ScaleResponseDto toResponseDto(Scale entity) {
        return ScaleResponseDto.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .location(entity.getLocation())
                .branch(branchMapper.toResponseDto(entity.getBranch()))
                .active(entity.getActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public void updateEntityFromDto(ScaleRequestDto dto, Scale entity, Branch branch) {
        entity.setCode(dto.getCode());
        entity.setLocation(dto.getLocation());
        entity.setBranch(branch);
        if (dto.getActive() != null) {
            entity.setActive(dto.getActive());
        }
    }
}
