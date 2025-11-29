package br.com.challenge.weighing_management_service.controller;

import br.com.challenge.weighing_management_service.dto.ApiResponse;
import br.com.challenge.weighing_management_service.dto.GrainTypeRequestDto;
import br.com.challenge.weighing_management_service.dto.GrainTypeResponseDto;
import br.com.challenge.weighing_management_service.service.GrainTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Grain Types", description = "Grain type management endpoints")
@RestController
@RequestMapping("/grain-types")
@RequiredArgsConstructor
public class GrainTypeController {

    private final GrainTypeService grainTypeService;

    @Operation(summary = "Create grain type", description = "Creates a new grain type with name, description and purchase price per ton")
    @PostMapping
    public ResponseEntity<ApiResponse<GrainTypeResponseDto>> create(@Valid @RequestBody GrainTypeRequestDto requestDto) {
        GrainTypeResponseDto responseDto = grainTypeService.create(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Grain type created successfully", responseDto));
    }

    @Operation(summary = "Get grain type by ID", description = "Returns a single grain type by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GrainTypeResponseDto>> getById(
            @Parameter(description = "Grain type ID") @PathVariable Long id) {
        GrainTypeResponseDto responseDto = grainTypeService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(responseDto));
    }

    @Operation(summary = "Get all grain types", description = "Returns a list of all grain types")
    @GetMapping
    public ResponseEntity<ApiResponse<List<GrainTypeResponseDto>>> getAll() {
        List<GrainTypeResponseDto> grainTypes = grainTypeService.getAll();
        return ResponseEntity.ok(ApiResponse.success(grainTypes));
    }

    @Operation(summary = "Update grain type", description = "Updates an existing grain type")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<GrainTypeResponseDto>> update(
            @Parameter(description = "Grain type ID") @PathVariable Long id,
            @Valid @RequestBody GrainTypeRequestDto requestDto) {
        GrainTypeResponseDto responseDto = grainTypeService.update(id, requestDto);
        return ResponseEntity.ok(ApiResponse.success("Grain type updated successfully", responseDto));
    }

    @Operation(summary = "Delete grain type", description = "Deletes a grain type by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @Parameter(description = "Grain type ID") @PathVariable Long id) {
        grainTypeService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Grain type deleted successfully", null));
    }
}