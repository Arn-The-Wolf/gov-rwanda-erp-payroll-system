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
import rw.gov.erp.payroll.dto.request.EmploymentRequest;
import rw.gov.erp.payroll.dto.response.ApiResponse;
import rw.gov.erp.payroll.dto.response.EmploymentResponse;
import rw.gov.erp.payroll.service.EmploymentService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employment")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Employment", description = "Employment management APIs")
public class EmploymentController {
    
    private final EmploymentService employmentService;
    
    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Create employment", description = "Create employment record (MANAGER only)")
    public ResponseEntity<ApiResponse<EmploymentResponse>> createEmployment(@Valid @RequestBody EmploymentRequest request) {
        EmploymentResponse response = employmentService.createEmployment(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Employment created successfully", response));
    }
    
    @GetMapping
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Get all employments", description = "Retrieve all employment records (MANAGER only)")
    public ResponseEntity<ApiResponse<List<EmploymentResponse>>> getAllEmployments() {
        List<EmploymentResponse> employments = employmentService.getAllEmployments();
        return ResponseEntity.ok(ApiResponse.success("Employments retrieved successfully", employments));
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Get employment by ID", description = "Retrieve employment by ID (MANAGER only)")
    public ResponseEntity<ApiResponse<EmploymentResponse>> getEmploymentById(@PathVariable Long id) {
        EmploymentResponse response = employmentService.getEmploymentById(id);
        return ResponseEntity.ok(ApiResponse.success("Employment retrieved successfully", response));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Update employment", description = "Update employment record (MANAGER only)")
    public ResponseEntity<ApiResponse<EmploymentResponse>> updateEmployment(
            @PathVariable Long id,
            @Valid @RequestBody EmploymentRequest request) {
        EmploymentResponse response = employmentService.updateEmployment(id, request);
        return ResponseEntity.ok(ApiResponse.success("Employment updated successfully", response));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Delete employment", description = "Delete employment record (MANAGER only)")
    public ResponseEntity<ApiResponse<Void>> deleteEmployment(@PathVariable Long id) {
        employmentService.deleteEmployment(id);
        return ResponseEntity.ok(ApiResponse.success("Employment deleted successfully"));
    }
}
