package br.com.challenge.weighing_management_service.service;

import br.com.challenge.weighing_management_service.dto.GrainTypeRequestDto;
import br.com.challenge.weighing_management_service.dto.GrainTypeResponseDto;
import br.com.challenge.weighing_management_service.entity.GrainType;
import br.com.challenge.weighing_management_service.exception.ResourceNotFoundException;
import br.com.challenge.weighing_management_service.mapper.GrainTypeMapper;
import br.com.challenge.weighing_management_service.repository.GrainTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GrainTypeService {

    private final GrainTypeRepository grainTypeRepository;
    private final GrainTypeMapper grainTypeMapper;

    @Transactional
    public GrainTypeResponseDto create(GrainTypeRequestDto requestDto) {
        log.info("Creating grain type: {}", requestDto.getName());

        GrainType grainType = grainTypeMapper.toEntity(requestDto);
        GrainType savedGrainType = grainTypeRepository.save(grainType);

        log.info("Grain type created with ID: {}", savedGrainType.getId());
        return grainTypeMapper.toResponseDto(savedGrainType);
    }

    @Transactional(readOnly = true)
    public GrainTypeResponseDto getById(Long id) {
        log.info("Fetching grain type with ID: {}", id);

        GrainType grainType = grainTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Grain type not found with ID: " + id));

        return grainTypeMapper.toResponseDto(grainType);
    }

    @Transactional(readOnly = true)
    public List<GrainTypeResponseDto> getAll() {
        log.info("Fetching all grain types");

        return grainTypeRepository.findAll().stream()
                .map(grainTypeMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public GrainTypeResponseDto update(Long id, GrainTypeRequestDto requestDto) {
        log.info("Updating grain type with ID: {}", id);

        GrainType grainType = grainTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Grain type not found with ID: " + id));

        grainTypeMapper.updateEntityFromDto(requestDto, grainType);
        GrainType updatedGrainType = grainTypeRepository.save(grainType);

        log.info("Grain type updated: {}", updatedGrainType.getId());
        return grainTypeMapper.toResponseDto(updatedGrainType);
    }

    @Transactional
    public void delete(Long id) {
        log.info("Deleting grain type with ID: {}", id);

        if (!grainTypeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Grain type not found with ID: " + id);
        }

        grainTypeRepository.deleteById(id);
        log.info("Grain type deleted: {}", id);
    }
}