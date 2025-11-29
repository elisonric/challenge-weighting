package br.com.challenge.weighing_management_service.mapper;

import br.com.challenge.weighing_management_service.dto.TruckRequestDto;
import br.com.challenge.weighing_management_service.dto.TruckResponseDto;
import br.com.challenge.weighing_management_service.entity.Branch;
import br.com.challenge.weighing_management_service.entity.Truck;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TruckMapper {

    private final BranchMapper branchMapper;

    public Truck toEntity(TruckRequestDto dto, Branch branch) {
        return Truck.builder()
                .plate(dto.getPlate())
                .model(dto.getModel())
                .tareWeight(dto.getTareWeight())
                .branch(branch)
                .build();
    }

    public TruckResponseDto toResponseDto(Truck entity) {
        return TruckResponseDto.builder()
                .id(entity.getId())
                .plate(entity.getPlate())
                .model(entity.getModel())
                .tareWeight(entity.getTareWeight())
                .branch(branchMapper.toResponseDto(entity.getBranch()))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public void updateEntityFromDto(TruckRequestDto dto, Truck entity, Branch branch) {
        entity.setPlate(dto.getPlate());
        entity.setModel(dto.getModel());
        entity.setTareWeight(dto.getTareWeight());
        entity.setBranch(branch);
    }
}