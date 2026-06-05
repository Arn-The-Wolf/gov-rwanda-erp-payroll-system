package rw.gov.erp.payroll.service;

import rw.gov.erp.payroll.dto.request.ForgotPasswordRequest;
import rw.gov.erp.payroll.dto.request.ResetPasswordRequest;
import rw.gov.erp.payroll.dto.response.ApiResponse;

public interface PasswordResetService {
    ApiResponse<String> forgotPassword(ForgotPasswordRequest request);
    ApiResponse<String> resetPassword(ResetPasswordRequest request);
    ApiResponse<String> validateResetToken(String token);
    void cleanupExpiredTokens();
}
