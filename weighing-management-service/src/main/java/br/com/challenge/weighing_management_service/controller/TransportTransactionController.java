package br.com.challenge.weighing_management_service.controller;

import br.com.challenge.weighing_management_service.dto.ApiResponse;
import br.com.challenge.weighing_management_service.dto.TransportTransactionRequestDto;
import br.com.challenge.weighing_management_service.dto.TransportTransactionResponseDto;
import br.com.challenge.weighing_management_service.service.TransportTransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Transport Transactions", description = "Transport transaction management endpoints")
@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransportTransactionController {

    private final TransportTransactionService transactionService;

    @Operation(summary = "Create transport transaction", description = "Creates a new transport transaction")
    @PostMapping
    public ResponseEntity<ApiResponse<TransportTransactionResponseDto>> create(@Valid @RequestBody TransportTransactionRequestDto requestDto) {
        TransportTransactionResponseDto responseDto = transactionService.create(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Transaction created successfully", responseDto));
    }

    @Operation(
        summary = "Search transactions",
        description = "Search transport transactions with optional filters. If no parameters are provided, returns all transactions. " +
                "Parameters: id (transaction ID), truckId (filter by truck), branchId (filter by branch), status (filter by status)"
    )
    @GetMapping
    public ResponseEntity<ApiResponse<List<TransportTransactionResponseDto>>> search(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) Long truckId,
            @RequestParam(required = false) Long branchId,
            @RequestParam(required = false) String status
    ) {
        List<TransportTransactionResponseDto> transactions = transactionService.search(id, truckId, branchId, status);
        return ResponseEntity.ok(ApiResponse.success(transactions));
    }

    @Operation(summary = "Complete transaction", description = "Marks a transaction as completed")
    @PatchMapping("/{id}/complete")
    public ResponseEntity<ApiResponse<TransportTransactionResponseDto>> complete(@PathVariable Long id) {
        TransportTransactionResponseDto responseDto = transactionService.complete(id);
        return ResponseEntity.ok(ApiResponse.success("Transaction completed successfully", responseDto));
    }

    @Operation(summary = "Cancel transaction", description = "Marks a transaction as cancelled")
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<TransportTransactionResponseDto>> cancel(@PathVariable Long id) {
        TransportTransactionResponseDto responseDto = transactionService.cancel(id);
        return ResponseEntity.ok(ApiResponse.success("Transaction cancelled successfully", responseDto));
    }

    @Operation(summary = "Delete transaction", description = "Deletes a transaction by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        transactionService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Transaction deleted successfully", null));
    }
}
