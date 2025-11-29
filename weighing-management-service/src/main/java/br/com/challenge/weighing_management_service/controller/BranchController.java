package br.com.challenge.weighing_management_service.controller;

import br.com.challenge.weighing_management_service.dto.ApiResponse;
import br.com.challenge.weighing_management_service.dto.BranchRequestDto;
import br.com.challenge.weighing_management_service.dto.BranchResponseDto;
import br.com.challenge.weighing_management_service.service.BranchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Branches", description = "Branch management endpoints")
@RestController
@RequestMapping("/branches")
@RequiredArgsConstructor
public class BranchController {

    private final BranchService branchService;

    @Operation(summary = "Create branch", description = "Creates a new branch")
    @PostMapping
    public ResponseEntity<ApiResponse<BranchResponseDto>> create(@Valid @RequestBody BranchRequestDto requestDto) {
        BranchResponseDto responseDto = branchService.create(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Branch created successfully", responseDto));
    }

    @Operation(summary = "Get branch by ID", description = "Returns a single branch by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BranchResponseDto>> getById(@PathVariable Long id) {
        BranchResponseDto responseDto = branchService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(responseDto));
    }

    @Operation(summary = "Get all branches", description = "Returns a list of all branches")
    @GetMapping
    public ResponseEntity<ApiResponse<List<BranchResponseDto>>> getAll() {
        List<BranchResponseDto> branches = branchService.getAll();
        return ResponseEntity.ok(ApiResponse.success(branches));
    }

    @Operation(summary = "Update branch", description = "Updates an existing branch")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BranchResponseDto>> update(
            @PathVariable Long id,
            @Valid @RequestBody BranchRequestDto requestDto) {
        BranchResponseDto responseDto = branchService.update(id, requestDto);
        return ResponseEntity.ok(ApiResponse.success("Branch updated successfully", responseDto));
    }

    @Operation(summary = "Delete branch", description = "Deletes a branch by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        branchService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Branch deleted successfully", null));
    }
}