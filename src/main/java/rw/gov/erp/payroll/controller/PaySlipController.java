package rw.gov.erp.payroll.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import rw.gov.erp.payroll.dto.response.ApiResponse;
import rw.gov.erp.payroll.dto.response.PaySlipResponse;
import rw.gov.erp.payroll.service.PaySlipService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payslips")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "PaySlip", description = "PaySlip management APIs")
public class PaySlipController {
    
    private final PaySlipService paySlipService;
    
    @PostMapping("/generate/{month}/{year}")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Generate payroll", description = "Generate payroll for all active employees for given month/year (MANAGER only)")
    public ResponseEntity<ApiResponse<List<PaySlipResponse>>> generatePayroll(
            @PathVariable Integer month,
            @PathVariable Integer year) {
        List<PaySlipResponse> paySlips = paySlipService.generatePayroll(month, year);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        String.format("Payroll generated successfully for %d/%d", month, year), 
                        paySlips));
    }
    
    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN', 'EMPLOYEE')")
    @Operation(summary = "Get payslips", description = "Get payslips (MANAGER/ADMIN see all, EMPLOYEE sees own)")
    public ResponseEntity<ApiResponse<List<PaySlipResponse>>> getPaySlips(Authentication authentication) {
        String email = authentication.getName();
        List<String> roles = authentication.getAuthorities().stream()
                .map(auth -> auth.getAuthority())
                .toList();
        
        List<PaySlipResponse> paySlips;
        if (roles.contains("ROLE_MANAGER") || roles.contains("ROLE_ADMIN")) {
            paySlips = paySlipService.getAllPaySlips();
        } else {
            paySlips = paySlipService.getMyPaySlips(email);
        }
        
        return ResponseEntity.ok(ApiResponse.success("PaySlips retrieved successfully", paySlips));
    }
    
    @GetMapping("/{employeeId}/{month}/{year}")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @Operation(summary = "Get payslip by employee and period", description = "Get specific employee payslip (MANAGER, ADMIN only)")
    public ResponseEntity<ApiResponse<PaySlipResponse>> getPaySlipByEmployeeAndPeriod(
            @PathVariable Long employeeId,
            @PathVariable Integer month,
            @PathVariable Integer year) {
        PaySlipResponse paySlip = paySlipService.getPaySlipByEmployeeAndPeriod(employeeId, month, year);
        return ResponseEntity.ok(ApiResponse.success("PaySlip retrieved successfully", paySlip));
    }
    
    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Approve payslip", description = "Approve payslip and send email notification (ADMIN only)")
    public ResponseEntity<ApiResponse<PaySlipResponse>> approvePaySlip(@PathVariable Long id) {
        PaySlipResponse paySlip = paySlipService.approvePaySlip(id);
        return ResponseEntity.ok(ApiResponse.success("PaySlip approved successfully", paySlip));
    }
}
