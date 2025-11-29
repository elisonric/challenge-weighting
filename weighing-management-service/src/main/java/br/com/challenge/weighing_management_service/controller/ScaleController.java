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

    @Operation(summary = "Get scale by ID", description = "Returns a single scale by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ScaleResponseDto>> getById(@PathVariable Long id) {
        ScaleResponseDto responseDto = scaleService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(responseDto));
    }

    @Operation(summary = "Get scale by code", description = "Returns a single scale by code")
    @GetMapping("/code/{code}")
    public ResponseEntity<ApiResponse<ScaleResponseDto>> getByCode(@PathVariable String code) {
        ScaleResponseDto responseDto = scaleService.getByCode(code);
        return ResponseEntity.ok(ApiResponse.success(responseDto));
    }

    @Operation(summary = "Get all scales", description = "Returns a list of all scales")
    @GetMapping
    public ResponseEntity<ApiResponse<List<ScaleResponseDto>>> getAll() {
        List<ScaleResponseDto> scales = scaleService.getAll();
        return ResponseEntity.ok(ApiResponse.success(scales));
    }

    @Operation(summary = "Get scales by branch", description = "Returns scales by branch ID")
    @GetMapping("/branch/{branchId}")
    public ResponseEntity<ApiResponse<List<ScaleResponseDto>>> getByBranchId(@PathVariable Long branchId) {
        List<ScaleResponseDto> scales = scaleService.getByBranchId(branchId);
        return ResponseEntity.ok(ApiResponse.success(scales));
    }

    @Operation(summary = "Get active scales", description = "Returns only active scales")
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<ScaleResponseDto>>> getActiveScales() {
        List<ScaleResponseDto> scales = scaleService.getActiveScales();
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
