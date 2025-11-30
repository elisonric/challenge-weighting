package br.com.challenge.weighing_management_service.service;

import br.com.challenge.weighing_management_service.dto.ScaleRequestDto;
import br.com.challenge.weighing_management_service.dto.ScaleResponseDto;
import br.com.challenge.weighing_management_service.entity.Branch;
import br.com.challenge.weighing_management_service.entity.Scale;
import br.com.challenge.weighing_management_service.exception.ResourceNotFoundException;
import br.com.challenge.weighing_management_service.mapper.ScaleMapper;
import br.com.challenge.weighing_management_service.repository.BranchRepository;
import br.com.challenge.weighing_management_service.repository.ScaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScaleService {

    private final ScaleRepository scaleRepository;
    private final BranchRepository branchRepository;
    private final ScaleMapper scaleMapper;

    @Transactional
    public ScaleResponseDto create(ScaleRequestDto requestDto) {
        Branch branch = branchRepository.findById(requestDto.getBranchId())
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found with id: " + requestDto.getBranchId()));

        Scale scale = scaleMapper.toEntity(requestDto, branch);
        Scale savedScale = scaleRepository.save(scale);
        return scaleMapper.toResponseDto(savedScale);
    }

    @Transactional(readOnly = true)
    public ScaleResponseDto getById(Long id) {
        Scale scale = scaleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Scale not found with id: " + id));
        return scaleMapper.toResponseDto(scale);
    }

    @Transactional(readOnly = true)
    public ScaleResponseDto getByCode(String code) {
        Scale scale = scaleRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Scale not found with code: " + code));
        return scaleMapper.toResponseDto(scale);
    }

    @Transactional(readOnly = true)
    public List<ScaleResponseDto> getAll() {
        return scaleRepository.findAll().stream()
                .map(scaleMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ScaleResponseDto> getByBranchId(Long branchId) {
        return scaleRepository.findByBranchId(branchId).stream()
                .map(scaleMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ScaleResponseDto> getActiveScales() {
        return scaleRepository.findByActive(true).stream()
                .map(scaleMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ScaleResponseDto> search(Long id, String code, Long branchId, Boolean activeOnly) {
        // If ID is provided, return single result as list
        if (id != null) {
            return List.of(getById(id));
        }

        // If code is provided, return single result as list
        if (code != null) {
            return List.of(getByCode(code));
        }

        // If branchId is provided
        if (branchId != null) {
            List<Scale> scales = scaleRepository.findByBranchId(branchId);
            // If activeOnly is also true, filter active scales
            if (Boolean.TRUE.equals(activeOnly)) {
                scales = scales.stream()
                        .filter(Scale::getActive)
                        .collect(Collectors.toList());
            }
            return scales.stream()
                    .map(scaleMapper::toResponseDto)
                    .collect(Collectors.toList());
        }

        // If only activeOnly is provided
        if (Boolean.TRUE.equals(activeOnly)) {
            return getActiveScales();
        }

        // No filters, return all
        return getAll();
    }

    @Transactional
    public ScaleResponseDto update(Long id, ScaleRequestDto requestDto) {
        Scale scale = scaleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Scale not found with id: " + id));

        Branch branch = branchRepository.findById(requestDto.getBranchId())
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found with id: " + requestDto.getBranchId()));

        scaleMapper.updateEntityFromDto(requestDto, scale, branch);
        Scale updatedScale = scaleRepository.save(scale);
        return scaleMapper.toResponseDto(updatedScale);
    }

    @Transactional
    public void delete(Long id) {
        if (!scaleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Scale not found with id: " + id);
        }
        scaleRepository.deleteById(id);
    }
}
