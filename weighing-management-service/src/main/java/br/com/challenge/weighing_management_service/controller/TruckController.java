package br.com.challenge.weighing_management_service.controller;

import br.com.challenge.weighing_management_service.dto.ApiResponse;
import br.com.challenge.weighing_management_service.dto.TruckRequestDto;
import br.com.challenge.weighing_management_service.dto.TruckResponseDto;
import br.com.challenge.weighing_management_service.service.TruckService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Trucks", description = "Truck management endpoints")
@RestController
@RequestMapping("/trucks")
@RequiredArgsConstructor
public class TruckController {

    private final TruckService truckService;

    @Operation(summary = "Create truck", description = "Creates a new truck with plate, model, tare weight and branch")
    @PostMapping
    public ResponseEntity<ApiResponse<TruckResponseDto>> create(@Valid @RequestBody TruckRequestDto requestDto) {
        TruckResponseDto responseDto = truckService.create(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Truck created successfully", responseDto));
    }

    @Operation(
        summary = "Search trucks",
        description = "Search trucks with optional filters. If no parameters are provided, returns all trucks. " +
                "Parameters: id (truck ID), plate (license plate)"
    )
    @GetMapping
    public ResponseEntity<ApiResponse<List<TruckResponseDto>>> search(
            @Parameter(description = "Truck ID") @RequestParam(required = false) Long id,
            @Parameter(description = "License plate") @RequestParam(required = false) String plate
    ) {
        List<TruckResponseDto> trucks = truckService.search(id, plate);
        return ResponseEntity.ok(ApiResponse.success(trucks));
    }

    @Operation(summary = "Update truck", description = "Updates an existing truck")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TruckResponseDto>> update(
            @Parameter(description = "Truck ID") @PathVariable Long id,
            @Valid @RequestBody TruckRequestDto requestDto) {
        TruckResponseDto responseDto = truckService.update(id, requestDto);
        return ResponseEntity.ok(ApiResponse.success("Truck updated successfully", responseDto));
    }

    @Operation(summary = "Delete truck", description = "Deletes a truck by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @Parameter(description = "Truck ID") @PathVariable Long id) {
        truckService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Truck deleted successfully", null));
    }
}