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

    @Operation(summary = "Get transaction by ID", description = "Returns a single transaction by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TransportTransactionResponseDto>> getById(@PathVariable Long id) {
        TransportTransactionResponseDto responseDto = transactionService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(responseDto));
    }

    @Operation(summary = "Get all transactions", description = "Returns a list of all transactions")
    @GetMapping
    public ResponseEntity<ApiResponse<List<TransportTransactionResponseDto>>> getAll() {
        List<TransportTransactionResponseDto> transactions = transactionService.getAll();
        return ResponseEntity.ok(ApiResponse.success(transactions));
    }

    @Operation(summary = "Get transactions by truck", description = "Returns transactions by truck ID")
    @GetMapping("/truck/{truckId}")
    public ResponseEntity<ApiResponse<List<TransportTransactionResponseDto>>> getByTruckId(@PathVariable Long truckId) {
        List<TransportTransactionResponseDto> transactions = transactionService.getByTruckId(truckId);
        return ResponseEntity.ok(ApiResponse.success(transactions));
    }

    @Operation(summary = "Get transactions by branch", description = "Returns transactions by branch ID")
    @GetMapping("/branch/{branchId}")
    public ResponseEntity<ApiResponse<List<TransportTransactionResponseDto>>> getByBranchId(@PathVariable Long branchId) {
        List<TransportTransactionResponseDto> transactions = transactionService.getByBranchId(branchId);
        return ResponseEntity.ok(ApiResponse.success(transactions));
    }

    @Operation(summary = "Get in-progress transactions", description = "Returns all in-progress transactions")
    @GetMapping("/in-progress")
    public ResponseEntity<ApiResponse<List<TransportTransactionResponseDto>>> getInProgressTransactions() {
        List<TransportTransactionResponseDto> transactions = transactionService.getInProgressTransactions();
        return ResponseEntity.ok(ApiResponse.success(transactions));
    }

    @Operation(summary = "Get transactions by status", description = "Returns transactions by status")
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<TransportTransactionResponseDto>>> getByStatus(@PathVariable String status) {
        List<TransportTransactionResponseDto> transactions = transactionService.getByStatus(status);
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
