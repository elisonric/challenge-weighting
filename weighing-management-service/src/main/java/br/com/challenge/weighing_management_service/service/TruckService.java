package br.com.challenge.weighing_management_service.service;

import br.com.challenge.weighing_management_service.dto.TruckRequestDto;
import br.com.challenge.weighing_management_service.dto.TruckResponseDto;
import br.com.challenge.weighing_management_service.entity.Branch;
import br.com.challenge.weighing_management_service.entity.Truck;
import br.com.challenge.weighing_management_service.exception.ResourceNotFoundException;
import br.com.challenge.weighing_management_service.mapper.TruckMapper;
import br.com.challenge.weighing_management_service.repository.BranchRepository;
import br.com.challenge.weighing_management_service.repository.TruckRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TruckService {

    private final TruckRepository truckRepository;
    private final BranchRepository branchRepository;
    private final TruckMapper truckMapper;

    @Transactional
    public TruckResponseDto create(TruckRequestDto requestDto) {
        log.info("Creating truck: {}", requestDto.getPlate());

        Branch branch = branchRepository.findById(requestDto.getBranchId())
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found with ID: " + requestDto.getBranchId()));

        Truck truck = truckMapper.toEntity(requestDto, branch);
        Truck savedTruck = truckRepository.save(truck);

        log.info("Truck created with ID: {}", savedTruck.getId());
        return truckMapper.toResponseDto(savedTruck);
    }

    @Transactional(readOnly = true)
    public TruckResponseDto getById(Long id) {
        log.info("Fetching truck with ID: {}", id);

        Truck truck = truckRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Truck not found with ID: " + id));

        return truckMapper.toResponseDto(truck);
    }

    @Transactional(readOnly = true)
    public TruckResponseDto getByPlate(String plate) {
        log.info("Fetching truck with plate: {}", plate);

        Truck truck = truckRepository.findByPlate(plate)
                .orElseThrow(() -> new ResourceNotFoundException("Truck not found with plate: " + plate));

        return truckMapper.toResponseDto(truck);
    }

    @Transactional(readOnly = true)
    public List<TruckResponseDto> getAll() {
        log.info("Fetching all trucks");

        return truckRepository.findAll().stream()
                .map(truckMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TruckResponseDto> search(Long id, String plate) {
        log.info("Searching trucks with filters - id: {}, plate: {}", id, plate);

        // If ID is provided, return single result as list
        if (id != null) {
            return List.of(getById(id));
        }

        // If plate is provided, return single result as list
        if (plate != null && !plate.isBlank()) {
            return List.of(getByPlate(plate));
        }

        // No filters, return all
        return getAll();
    }

    @Transactional
    public TruckResponseDto update(Long id, TruckRequestDto requestDto) {
        log.info("Updating truck with ID: {}", id);

        Truck truck = truckRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Truck not found with ID: " + id));

        Branch branch = branchRepository.findById(requestDto.getBranchId())
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found with ID: " + requestDto.getBranchId()));

        truckMapper.updateEntityFromDto(requestDto, truck, branch);
        Truck updatedTruck = truckRepository.save(truck);

        log.info("Truck updated: {}", updatedTruck.getId());
        return truckMapper.toResponseDto(updatedTruck);
    }

    @Transactional
    public void delete(Long id) {
        log.info("Deleting truck with ID: {}", id);

        if (!truckRepository.existsById(id)) {
            throw new ResourceNotFoundException("Truck not found with ID: " + id);
        }

        truckRepository.deleteById(id);
        log.info("Truck deleted: {}", id);
    }
}