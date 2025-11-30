package br.com.challenge.weighing_management_service.service;

import br.com.challenge.weighing_management_service.dto.TransportTransactionRequestDto;
import br.com.challenge.weighing_management_service.dto.TransportTransactionResponseDto;
import br.com.challenge.weighing_management_service.entity.Branch;
import br.com.challenge.weighing_management_service.entity.GrainType;
import br.com.challenge.weighing_management_service.entity.TransportTransaction;
import br.com.challenge.weighing_management_service.entity.Truck;
import br.com.challenge.weighing_management_service.entity.enums.TransactionStatus;
import br.com.challenge.weighing_management_service.exception.ResourceNotFoundException;
import br.com.challenge.weighing_management_service.mapper.TransportTransactionMapper;
import br.com.challenge.weighing_management_service.repository.BranchRepository;
import br.com.challenge.weighing_management_service.repository.GrainTypeRepository;
import br.com.challenge.weighing_management_service.repository.TransportTransactionRepository;
import br.com.challenge.weighing_management_service.repository.TruckRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransportTransactionService {

    private final TransportTransactionRepository transactionRepository;
    private final TruckRepository truckRepository;
    private final GrainTypeRepository grainTypeRepository;
    private final BranchRepository branchRepository;
    private final TransportTransactionMapper transactionMapper;

    @Transactional
    public TransportTransactionResponseDto create(TransportTransactionRequestDto requestDto) {
        Truck truck = truckRepository.findById(requestDto.getTruckId())
                .orElseThrow(() -> new ResourceNotFoundException("Truck not found with id: " + requestDto.getTruckId()));

        GrainType grainType = grainTypeRepository.findById(requestDto.getGrainTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Grain type not found with id: " + requestDto.getGrainTypeId()));

        Branch branch = branchRepository.findById(requestDto.getBranchId())
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found with id: " + requestDto.getBranchId()));

        TransportTransaction transaction = transactionMapper.toEntity(requestDto, truck, grainType, branch);
        TransportTransaction savedTransaction = transactionRepository.save(transaction);
        return transactionMapper.toResponseDto(savedTransaction);
    }

    @Transactional(readOnly = true)
    public TransportTransactionResponseDto getById(Long id) {
        TransportTransaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));
        return transactionMapper.toResponseDto(transaction);
    }

    @Transactional(readOnly = true)
    public List<TransportTransactionResponseDto> getAll() {
        return transactionRepository.findAll().stream()
                .map(transactionMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TransportTransactionResponseDto> getByTruckId(Long truckId) {
        return transactionRepository.findByTruckId(truckId).stream()
                .map(transactionMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TransportTransactionResponseDto> getByBranchId(Long branchId) {
        return transactionRepository.findByBranchId(branchId).stream()
                .map(transactionMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TransportTransactionResponseDto> getByStatus(String status) {
        TransactionStatus transactionStatus = TransactionStatus.valueOf(status.toUpperCase());
        return transactionRepository.findByStatus(transactionStatus).stream()
                .map(transactionMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TransportTransactionResponseDto> getInProgressTransactions() {
        return transactionRepository.findByStatus(TransactionStatus.IN_PROGRESS).stream()
                .map(transactionMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TransportTransactionResponseDto> search(Long id, Long truckId, Long branchId, String status) {
        // If ID is provided, return single result as list
        if (id != null) {
            return List.of(getById(id));
        }

        // Start with all transactions
        List<TransportTransaction> transactions = transactionRepository.findAll();

        // Apply filters cumulatively
        if (truckId != null) {
            transactions = transactions.stream()
                    .filter(t -> t.getTruck().getId().equals(truckId))
                    .collect(Collectors.toList());
        }

        if (branchId != null) {
            transactions = transactions.stream()
                    .filter(t -> t.getBranch().getId().equals(branchId))
                    .collect(Collectors.toList());
        }

        if (status != null && !status.isBlank()) {
            TransactionStatus transactionStatus = TransactionStatus.valueOf(status.toUpperCase());
            transactions = transactions.stream()
                    .filter(t -> t.getStatus() == transactionStatus)
                    .collect(Collectors.toList());
        }

        return transactions.stream()
                .map(transactionMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public TransportTransactionResponseDto complete(Long id) {
        TransportTransaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));

        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setEndTime(LocalDateTime.now());

        TransportTransaction updatedTransaction = transactionRepository.save(transaction);
        return transactionMapper.toResponseDto(updatedTransaction);
    }

    @Transactional
    public TransportTransactionResponseDto cancel(Long id) {
        TransportTransaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));

        transaction.setStatus(TransactionStatus.CANCELLED);
        transaction.setEndTime(LocalDateTime.now());

        TransportTransaction updatedTransaction = transactionRepository.save(transaction);
        return transactionMapper.toResponseDto(updatedTransaction);
    }

    @Transactional
    public void delete(Long id) {
        if (!transactionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Transaction not found with id: " + id);
        }
        transactionRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Optional<TransportTransaction> findInProgressTransactionByTruck(Long truckId) {
        List<TransportTransaction> inProgressTransactions = transactionRepository.findByTruckId(truckId).stream()
                .filter(t -> t.getStatus() == TransactionStatus.IN_PROGRESS)
                .collect(Collectors.toList());

        return inProgressTransactions.isEmpty() ? Optional.empty() : Optional.of(inProgressTransactions.get(0));
    }
}
