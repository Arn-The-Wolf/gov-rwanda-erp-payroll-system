package rw.gov.erp.payroll.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rw.gov.erp.payroll.dto.request.ForgotPasswordRequest;
import rw.gov.erp.payroll.dto.request.ResetPasswordRequest;
import rw.gov.erp.payroll.dto.response.ApiResponse;
import rw.gov.erp.payroll.service.PasswordResetService;

@RestController
@RequestMapping("/api/v1/password")
@RequiredArgsConstructor
@Tag(name = "Password Reset", description = "Password recovery and reset endpoints")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    @PostMapping("/forgot")
    @Operation(
            summary = "Forgot Password",
            description = "Request a password reset link. A secure link will be sent to the registered email address."
    )
    public ResponseEntity<ApiResponse<String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        return ResponseEntity.ok(passwordResetService.forgotPassword(request));
    }

    @PostMapping("/reset")
    @Operation(
            summary = "Reset Password",
            description = "Reset password using the token received via email. Password must be at least 8 characters with uppercase, lowercase, digit, and special character."
    )
    public ResponseEntity<ApiResponse<String>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        return ResponseEntity.ok(passwordResetService.resetPassword(request));
    }

    @GetMapping("/validate-token")
    @Operation(
            summary = "Validate Reset Token",
            description = "Check if a password reset token is still valid and not expired."
    )
    public ResponseEntity<ApiResponse<String>> validateToken(@RequestParam String token) {
        return ResponseEntity.ok(passwordResetService.validateResetToken(token));
    }
}
