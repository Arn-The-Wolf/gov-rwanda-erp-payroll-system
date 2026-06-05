package rw.gov.erp.payroll.service;

import rw.gov.erp.payroll.dto.request.LoginWithOtpRequest;
import rw.gov.erp.payroll.dto.request.OtpRequest;
import rw.gov.erp.payroll.dto.request.OtpVerifyRequest;
import rw.gov.erp.payroll.dto.response.ApiResponse;
import rw.gov.erp.payroll.dto.response.AuthResponse;

public interface OtpService {
    ApiResponse<String> generateOtp(OtpRequest request);
    ApiResponse<String> verifyOtp(OtpVerifyRequest request);
    AuthResponse loginWithOtp(LoginWithOtpRequest request);
    void cleanupExpiredOtps();
}
