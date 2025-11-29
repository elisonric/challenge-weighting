package br.com.challenge.weighing_management_service.mapper;

import br.com.challenge.weighing_management_service.dto.TransportTransactionRequestDto;
import br.com.challenge.weighing_management_service.dto.TransportTransactionResponseDto;
import br.com.challenge.weighing_management_service.entity.Branch;
import br.com.challenge.weighing_management_service.entity.GrainType;
import br.com.challenge.weighing_management_service.entity.TransportTransaction;
import br.com.challenge.weighing_management_service.entity.Truck;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransportTransactionMapper {

    private final TruckMapper truckMapper;
    private final GrainTypeMapper grainTypeMapper;
    private final BranchMapper branchMapper;

    public TransportTransaction toEntity(TransportTransactionRequestDto dto, Truck truck, GrainType grainType, Branch branch) {
        return TransportTransaction.builder()
                .truck(truck)
                .grainType(grainType)
                .branch(branch)
                .startTime(dto.getStartTime())
                .build();
    }

    public TransportTransactionResponseDto toResponseDto(TransportTransaction entity) {
        return TransportTransactionResponseDto.builder()
                .id(entity.getId())
                .truck(truckMapper.toResponseDto(entity.getTruck()))
                .grainType(grainTypeMapper.toResponseDto(entity.getGrainType()))
                .branch(branchMapper.toResponseDto(entity.getBranch()))
                .status(entity.getStatus().name())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
