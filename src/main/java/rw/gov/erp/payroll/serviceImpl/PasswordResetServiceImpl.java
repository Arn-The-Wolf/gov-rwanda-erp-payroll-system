package rw.gov.erp.payroll.serviceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rw.gov.erp.payroll.dto.request.ForgotPasswordRequest;
import rw.gov.erp.payroll.dto.request.ResetPasswordRequest;
import rw.gov.erp.payroll.dto.response.ApiResponse;
import rw.gov.erp.payroll.entity.Employee;
import rw.gov.erp.payroll.entity.PasswordResetToken;
import rw.gov.erp.payroll.exception.BadRequestException;
import rw.gov.erp.payroll.exception.ResourceNotFoundException;
import rw.gov.erp.payroll.repository.EmployeeRepository;
import rw.gov.erp.payroll.repository.PasswordResetTokenRepository;
import rw.gov.erp.payroll.service.EmailService;
import rw.gov.erp.payroll.service.PasswordResetService;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {

    private final PasswordResetTokenRepository tokenRepository;
    private final EmployeeRepository employeeRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.frontend.url:http://localhost:8080}")
    private String frontendUrl;

    private static final int TOKEN_EXPIRY_HOURS = 1;

    @Override
    @Transactional
    public ApiResponse<String> forgotPassword(ForgotPasswordRequest request) {
        // Find employee by email
        Employee employee = employeeRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("No account found with email: " + request.getEmail()));

        // Delete any existing tokens for this employee
        tokenRepository.deleteByEmployeeId(employee.getId());

        // Generate reset token
        String token = PasswordResetToken.generateToken();

        // Create password reset token
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .employee(employee)
                .expiresAt(LocalDateTime.now().plusHours(TOKEN_EXPIRY_HOURS))
                .used(false)
                .build();

        tokenRepository.save(resetToken);

        // Create reset link
        String resetLink = frontendUrl + "/reset-password?token=" + token;

        // Send email
        try {
            String subject = "Password Reset Request - ERP Payroll System";
            String body = String.format(
                    "Dear %s,\n\n" +
                    "We received a request to reset your password for your ERP Payroll System account.\n\n" +
                    "To reset your password, please click the link below:\n" +
                    "%s\n\n" +
                    "This link will expire in %d hour(s).\n\n" +
                    "If you didn't request this password reset, please ignore this email. " +
                    "Your password will remain unchanged.\n\n" +
                    "For security reasons, please do not share this link with anyone.\n\n" +
                    "Best regards,\n" +
                    "ERP Payroll System Team",
                    employee.getFirstName(),
                    resetLink,
                    TOKEN_EXPIRY_HOURS
            );

            emailService.sendEmail(request.getEmail(), subject, body);

            log.info("Password reset email sent to: {}", request.getEmail());

            return ApiResponse.<String>builder()
                    .success(true)
                    .message("Password reset link sent to your email")
                    .data("Please check your email to reset your password. Link expires in " + TOKEN_EXPIRY_HOURS + " hour(s).")
                    .build();

        } catch (Exception e) {
            log.error("Failed to send password reset email", e);
            throw new BadRequestException("Failed to send reset email. Please try again later.");
        }
    }

    @Override
    @Transactional
    public ApiResponse<String> resetPassword(ResetPasswordRequest request) {
        // Validate passwords match
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Passwords do not match");
        }

        // Find token
        PasswordResetToken resetToken = tokenRepository.findByTokenAndUsedFalse(request.getToken())
                .orElseThrow(() -> new BadRequestException("Invalid or expired reset token"));

        // Check if token is expired
        if (resetToken.isExpired()) {
            tokenRepository.delete(resetToken);
            throw new BadRequestException("Reset token has expired. Please request a new one.");
        }

        // Get employee
        Employee employee = resetToken.getEmployee();

        // Update password
        employee.setPassword(passwordEncoder.encode(request.getNewPassword()));
        employeeRepository.save(employee);

        // Mark token as used
        resetToken.setUsed(true);
        tokenRepository.save(resetToken);

        // Send confirmation email
        try {
            String subject = "Password Changed Successfully - ERP Payroll System";
            String body = String.format(
                    "Dear %s,\n\n" +
                    "Your password has been successfully changed.\n\n" +
                    "If you didn't make this change, please contact support immediately.\n\n" +
                    "Best regards,\n" +
                    "ERP Payroll System Team",
                    employee.getFirstName()
            );

            emailService.sendEmail(employee.getEmail(), subject, body);
        } catch (Exception e) {
            log.error("Failed to send password change confirmation email", e);
            // Don't fail the operation if email fails
        }

        log.info("Password reset successfully for user: {}", employee.getEmail());

        return ApiResponse.<String>builder()
                .success(true)
                .message("Password reset successfully")
                .data("You can now login with your new password")
                .build();
    }

    @Override
    public ApiResponse<String> validateResetToken(String token) {
        PasswordResetToken resetToken = tokenRepository.findByTokenAndUsedFalse(token)
                .orElseThrow(() -> new BadRequestException("Invalid or expired reset token"));

        if (resetToken.isExpired()) {
            tokenRepository.delete(resetToken);
            throw new BadRequestException("Reset token has expired. Please request a new one.");
        }

        return ApiResponse.<String>builder()
                .success(true)
                .message("Token is valid")
                .data("You can proceed with password reset")
                .build();
    }

    @Override
    @Transactional
    @Scheduled(fixedRate = 3600000) // Run every hour
    public void cleanupExpiredTokens() {
        try {
            tokenRepository.deleteByExpiresAtBefore(LocalDateTime.now());
            log.debug("Cleaned up expired password reset tokens");
        } catch (Exception e) {
            log.error("Error cleaning up expired tokens", e);
        }
    }
}
