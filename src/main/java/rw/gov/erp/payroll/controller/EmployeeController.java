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
import rw.gov.erp.payroll.dto.request.EmployeeRequest;
import rw.gov.erp.payroll.dto.response.ApiResponse;
import rw.gov.erp.payroll.dto.response.EmployeeResponse;
import rw.gov.erp.payroll.service.EmployeeService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Employee", description = "Employee management APIs")
public class EmployeeController {
    
    private final EmployeeService employeeService;
    
    @PostMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @Operation(summary = "Create employee", description = "Create a new employee (MANAGER, ADMIN only)")
    public ResponseEntity<ApiResponse<EmployeeResponse>> createEmployee(@Valid @RequestBody EmployeeRequest request) {
        EmployeeResponse response = employeeService.createEmployee(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Employee created successfully", response));
    }
    
    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @Operation(summary = "Get all employees", description = "Retrieve all employees (MANAGER, ADMIN only)")
    public ResponseEntity<ApiResponse<List<EmployeeResponse>>> getAllEmployees() {
        List<EmployeeResponse> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(ApiResponse.success("Employees retrieved successfully", employees));
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @Operation(summary = "Get employee by ID", description = "Retrieve employee by ID (MANAGER, ADMIN only)")
    public ResponseEntity<ApiResponse<EmployeeResponse>> getEmployeeById(@PathVariable Long id) {
        EmployeeResponse response = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(ApiResponse.success("Employee retrieved successfully", response));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @Operation(summary = "Update employee", description = "Update employee details (MANAGER, ADMIN only)")
    public ResponseEntity<ApiResponse<EmployeeResponse>> updateEmployee(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeRequest request) {
        EmployeeResponse response = employeeService.updateEmployee(id, request);
        return ResponseEntity.ok(ApiResponse.success("Employee updated successfully", response));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @Operation(summary = "Delete employee", description = "Delete employee (MANAGER, ADMIN only)")
    public ResponseEntity<ApiResponse<Void>> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.ok(ApiResponse.success("Employee deleted successfully"));
    }
    
    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @Operation(summary = "Activate employee", description = "Activate employee account (MANAGER, ADMIN only)")
    public ResponseEntity<ApiResponse<EmployeeResponse>> activateEmployee(@PathVariable Long id) {
        EmployeeResponse response = employeeService.activateEmployee(id);
        return ResponseEntity.ok(ApiResponse.success("Employee activated successfully", response));
    }
    
    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @Operation(summary = "Deactivate employee", description = "Deactivate employee account (MANAGER, ADMIN only)")
    public ResponseEntity<ApiResponse<EmployeeResponse>> deactivateEmployee(@PathVariable Long id) {
        EmployeeResponse response = employeeService.deactivateEmployee(id);
        return ResponseEntity.ok(ApiResponse.success("Employee deactivated successfully", response));
    }
}
