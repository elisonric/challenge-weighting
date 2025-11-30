package br.com.challenge.weighing_management_service.controller;

import br.com.challenge.weighing_management_service.dto.ApiResponse;
import br.com.challenge.weighing_management_service.dto.ScaleRequestDto;
import br.com.challenge.weighing_management_service.dto.ScaleResponseDto;
import br.com.challenge.weighing_management_service.service.ScaleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Scales", description = "Scale management endpoints")
@RestController
@RequestMapping("/scales")
@RequiredArgsConstructor
public class ScaleController {

    private final ScaleService scaleService;

    @Operation(summary = "Create scale", description = "Creates a new scale")
    @PostMapping
    public ResponseEntity<ApiResponse<ScaleResponseDto>> create(@Valid @RequestBody ScaleRequestDto requestDto) {
        ScaleResponseDto responseDto = scaleService.create(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Scale created successfully", responseDto));
    }

    @Operation(
        summary = "Search scales",
        description = "Search scales with optional filters. If no parameters are provided, returns all scales. " +
                "Parameters: id (scale ID), code (scale code), branchId (filter by branch), activeOnly (filter active scales only)"
    )
    @GetMapping
    public ResponseEntity<ApiResponse<List<ScaleResponseDto>>> search(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) Long branchId,
            @RequestParam(required = false) Boolean activeOnly
    ) {
        List<ScaleResponseDto> scales = scaleService.search(id, code, branchId, activeOnly);
        return ResponseEntity.ok(ApiResponse.success(scales));
    }

    @Operation(summary = "Update scale", description = "Updates an existing scale")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ScaleResponseDto>> update(
            @PathVariable Long id,
            @Valid @RequestBody ScaleRequestDto requestDto) {
        ScaleResponseDto responseDto = scaleService.update(id, requestDto);
        return ResponseEntity.ok(ApiResponse.success("Scale updated successfully", responseDto));
    }

    @Operation(summary = "Delete scale", description = "Deletes a scale by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        scaleService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Scale deleted successfully", null));
    }
}
