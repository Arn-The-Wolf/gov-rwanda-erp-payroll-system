package rw.gov.erp.payroll.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rw.gov.erp.payroll.dto.request.DeductionRequest;
import rw.gov.erp.payroll.dto.response.ApiResponse;
import rw.gov.erp.payroll.dto.response.DeductionResponse;
import rw.gov.erp.payroll.service.DeductionService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/deductions")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Deduction", description = "Deduction management APIs")
public class DeductionController {
    
    private final DeductionService deductionService;
    
    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Create deduction", description = "Create a new deduction (MANAGER only)")
    public ResponseEntity<ApiResponse<DeductionResponse>> createDeduction(@Valid @RequestBody DeductionRequest request) {
        DeductionResponse response = deductionService.createDeduction(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Deduction created successfully", response));
    }
    
    @GetMapping
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Get all deductions", description = "Retrieve all deductions (MANAGER only)")
    public ResponseEntity<ApiResponse<List<DeductionResponse>>> getAllDeductions() {
        List<DeductionResponse> deductions = deductionService.getAllDeductions();
        return ResponseEntity.ok(ApiResponse.success("Deductions retrieved successfully", deductions));
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Get deduction by ID", description = "Retrieve deduction by ID (MANAGER only)")
    public ResponseEntity<ApiResponse<DeductionResponse>> getDeductionById(@PathVariable Long id) {
        DeductionResponse response = deductionService.getDeductionById(id);
        return ResponseEntity.ok(ApiResponse.success("Deduction retrieved successfully", response));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Update deduction", description = "Update deduction details (MANAGER only)")
    public ResponseEntity<ApiResponse<DeductionResponse>> updateDeduction(
            @PathVariable Long id,
            @Valid @RequestBody DeductionRequest request) {
        DeductionResponse response = deductionService.updateDeduction(id, request);
        return ResponseEntity.ok(ApiResponse.success("Deduction updated successfully", response));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Delete deduction", description = "Delete deduction (MANAGER only)")
    public ResponseEntity<ApiResponse<Void>> deleteDeduction(@PathVariable Long id) {
        deductionService.deleteDeduction(id);
        return ResponseEntity.ok(ApiResponse.success("Deduction deleted successfully"));
    }
}
