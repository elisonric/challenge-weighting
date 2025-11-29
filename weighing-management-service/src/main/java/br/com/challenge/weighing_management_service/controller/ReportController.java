package br.com.challenge.weighing_management_service.controller;

import br.com.challenge.weighing_management_service.dto.ApiResponse;
import br.com.challenge.weighing_management_service.dto.CostReportDto;
import br.com.challenge.weighing_management_service.dto.ProfitReportDto;
import br.com.challenge.weighing_management_service.dto.WeighingReportDto;
import br.com.challenge.weighing_management_service.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "Reports", description = "Report endpoints for weighings, costs and profits analysis")
@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @Operation(summary = "Get weighings report", description = "Returns weighing records with optional filters")
    @GetMapping("/weighings")
    public ResponseEntity<ApiResponse<List<WeighingReportDto>>> getWeighings(
            @Parameter(description = "Filter by branch ID") @RequestParam(required = false) Long branchId,
            @Parameter(description = "Filter by truck ID") @RequestParam(required = false) Long truckId,
            @Parameter(description = "Filter by grain type ID") @RequestParam(required = false) Long grainTypeId,
            @Parameter(description = "Start date for period filter") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date for period filter") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        List<WeighingReportDto> report = reportService.getWeighings(branchId, truckId, grainTypeId, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(report));
    }

    @Operation(summary = "Get costs report", description = "Returns cost analysis grouped by specified field. GroupBy is required (branch, truck, or grainType)")
    @GetMapping("/costs")
    public ResponseEntity<ApiResponse<List<CostReportDto>>> getCosts(
            @Parameter(description = "Group by field (REQUIRED): branch, truck, or grainType", required = true) @RequestParam String groupBy,
            @Parameter(description = "Filter by branch ID") @RequestParam(required = false) Long branchId,
            @Parameter(description = "Filter by truck ID") @RequestParam(required = false) Long truckId,
            @Parameter(description = "Filter by grain type ID") @RequestParam(required = false) Long grainTypeId,
            @Parameter(description = "Start date for period filter") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date for period filter") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        List<CostReportDto> report = reportService.getCosts(groupBy, branchId, truckId, grainTypeId, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(report));
    }

    @Operation(summary = "Get profits report", description = "Returns profit analysis grouped by specified field. GroupBy is required (branch, truck, or grainType)")
    @GetMapping("/profits")
    public ResponseEntity<ApiResponse<List<ProfitReportDto>>> getProfits(
            @Parameter(description = "Group by field (REQUIRED): branch, truck, or grainType", required = true) @RequestParam String groupBy,
            @Parameter(description = "Filter by branch ID") @RequestParam(required = false) Long branchId,
            @Parameter(description = "Filter by truck ID") @RequestParam(required = false) Long truckId,
            @Parameter(description = "Filter by grain type ID") @RequestParam(required = false) Long grainTypeId,
            @Parameter(description = "Start date for period filter") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date for period filter") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        List<ProfitReportDto> report = reportService.getProfits(groupBy, branchId, truckId, grainTypeId, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(report));
    }
}