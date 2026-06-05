package rw.gov.erp.payroll.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rw.gov.erp.payroll.dto.request.LoginWithOtpRequest;
import rw.gov.erp.payroll.dto.request.OtpRequest;
import rw.gov.erp.payroll.dto.request.OtpVerifyRequest;
import rw.gov.erp.payroll.dto.response.ApiResponse;
import rw.gov.erp.payroll.dto.response.AuthResponse;
import rw.gov.erp.payroll.service.OtpService;

@RestController
@RequestMapping("/api/v1/otp")
@RequiredArgsConstructor
@Tag(name = "OTP Authentication", description = "One-Time Password authentication endpoints")
public class OtpController {

    private final OtpService otpService;

    @PostMapping("/generate")
    @Operation(
            summary = "Generate OTP",
            description = "Generate and send OTP code to user's email. OTP expires in 5 minutes."
    )
    public ResponseEntity<ApiResponse<String>> generateOtp(@Valid @RequestBody OtpRequest request) {
        return ResponseEntity.ok(otpService.generateOtp(request));
    }

    @PostMapping("/verify")
    @Operation(
            summary = "Verify OTP",
            description = "Verify the OTP code sent to user's email. Maximum 3 attempts allowed."
    )
    public ResponseEntity<ApiResponse<String>> verifyOtp(@Valid @RequestBody OtpVerifyRequest request) {
        return ResponseEntity.ok(otpService.verifyOtp(request));
    }

    @PostMapping("/login")
    @Operation(
            summary = "Login with OTP",
            description = "Login using verified OTP code. Returns JWT token for authentication."
    )
    public ResponseEntity<AuthResponse> loginWithOtp(@Valid @RequestBody LoginWithOtpRequest request) {
        return ResponseEntity.ok(otpService.loginWithOtp(request));
    }
}
