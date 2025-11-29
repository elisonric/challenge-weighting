package br.com.challenge.weighing_management_service.service;

import br.com.challenge.weighing_management_service.dto.BranchRequestDto;
import br.com.challenge.weighing_management_service.dto.BranchResponseDto;
import br.com.challenge.weighing_management_service.entity.Branch;
import br.com.challenge.weighing_management_service.exception.ResourceNotFoundException;
import br.com.challenge.weighing_management_service.mapper.BranchMapper;
import br.com.challenge.weighing_management_service.repository.BranchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BranchService {

    private final BranchRepository branchRepository;
    private final BranchMapper branchMapper;

    @Transactional
    public BranchResponseDto create(BranchRequestDto requestDto) {
        log.info("Creating branch: {}", requestDto.getName());

        Branch branch = branchMapper.toEntity(requestDto);
        Branch savedBranch = branchRepository.save(branch);

        log.info("Branch created with ID: {}", savedBranch.getId());
        return branchMapper.toResponseDto(savedBranch);
    }

    @Transactional(readOnly = true)
    public BranchResponseDto getById(Long id) {
        log.info("Fetching branch with ID: {}", id);

        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found with ID: " + id));

        return branchMapper.toResponseDto(branch);
    }

    @Transactional(readOnly = true)
    public List<BranchResponseDto> getAll() {
        log.info("Fetching all branches");

        return branchRepository.findAll().stream()
                .map(branchMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public BranchResponseDto update(Long id, BranchRequestDto requestDto) {
        log.info("Updating branch with ID: {}", id);

        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found with ID: " + id));

        branchMapper.updateEntityFromDto(requestDto, branch);
        Branch updatedBranch = branchRepository.save(branch);

        log.info("Branch updated: {}", updatedBranch.getId());
        return branchMapper.toResponseDto(updatedBranch);
    }

    @Transactional
    public void delete(Long id) {
        log.info("Deleting branch with ID: {}", id);

        if (!branchRepository.existsById(id)) {
            throw new ResourceNotFoundException("Branch not found with ID: " + id);
        }

        branchRepository.deleteById(id);
        log.info("Branch deleted: {}", id);
    }
}