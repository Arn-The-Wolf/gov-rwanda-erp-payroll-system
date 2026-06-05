package rw.gov.erp.payroll.serviceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rw.gov.erp.payroll.dto.request.LoginWithOtpRequest;
import rw.gov.erp.payroll.dto.request.OtpRequest;
import rw.gov.erp.payroll.dto.request.OtpVerifyRequest;
import rw.gov.erp.payroll.dto.response.ApiResponse;
import rw.gov.erp.payroll.dto.response.AuthResponse;
import rw.gov.erp.payroll.entity.Employee;
import rw.gov.erp.payroll.entity.Otp;
import rw.gov.erp.payroll.exception.BadRequestException;
import rw.gov.erp.payroll.exception.ResourceNotFoundException;
import rw.gov.erp.payroll.repository.EmployeeRepository;
import rw.gov.erp.payroll.repository.OtpRepository;
import rw.gov.erp.payroll.security.CustomUserDetailsService;
import rw.gov.erp.payroll.security.JwtTokenProvider;
import rw.gov.erp.payroll.service.EmailService;
import rw.gov.erp.payroll.service.OtpService;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private final OtpRepository otpRepository;
    private final EmployeeRepository employeeRepository;
    private final EmailService emailService;
    private final JwtTokenProvider tokenProvider;
    private final CustomUserDetailsService userDetailsService;

    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRY_MINUTES = 5;
    private static final int MAX_ATTEMPTS = 3;

    @Override
    @Transactional
    public ApiResponse<String> generateOtp(OtpRequest request) {
        // Check if employee exists
        Employee employee = employeeRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with email: " + request.getEmail()));

        // Delete any existing OTP for this email
        otpRepository.deleteByEmail(request.getEmail());

        // Generate 6-digit OTP
        String otpCode = generateOtpCode();

        // Create OTP entity
        Otp otp = Otp.builder()
                .email(request.getEmail())
                .code(otpCode)
                .expiresAt(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES))
                .verified(false)
                .attempts(0)
                .build();

        otpRepository.save(otp);

        // Send OTP via email
        try {
            String subject = "Your OTP Code - ERP Payroll System";
            String body = String.format(
                    "Dear %s,\n\n" +
                    "Your One-Time Password (OTP) for login is: %s\n\n" +
                    "This code will expire in %d minutes.\n\n" +
                    "If you didn't request this code, please ignore this email.\n\n" +
                    "Best regards,\n" +
                    "ERP Payroll System Team",
                    employee.getFirstName(), otpCode, OTP_EXPIRY_MINUTES
            );

            emailService.sendEmail(request.getEmail(), subject, body);

            log.info("OTP generated and sent to email: {}", request.getEmail());

            return ApiResponse.<String>builder()
                    .success(true)
                    .message("OTP sent successfully to " + maskEmail(request.getEmail()))
                    .data("OTP will expire in " + OTP_EXPIRY_MINUTES + " minutes")
                    .build();

        } catch (Exception e) {
            log.error("Failed to send OTP email", e);
            throw new BadRequestException("Failed to send OTP. Please try again later.");
        }
    }

    @Override
    @Transactional
    public ApiResponse<String> verifyOtp(OtpVerifyRequest request) {
        Otp otp = otpRepository.findByEmailAndVerifiedFalse(request.getEmail())
                .orElseThrow(() -> new BadRequestException("No active OTP found for this email"));

        // Check if OTP is expired
        if (otp.isExpired()) {
            otpRepository.delete(otp);
            throw new BadRequestException("OTP has expired. Please request a new one.");
        }

        // Check if max attempts reached
        if (otp.isMaxAttemptsReached()) {
            otpRepository.delete(otp);
            throw new BadRequestException("Maximum verification attempts exceeded. Please request a new OTP.");
        }

        // Verify OTP code
        if (!otp.getCode().equals(request.getCode())) {
            otp.setAttempts(otp.getAttempts() + 1);
            otpRepository.save(otp);

            int remainingAttempts = MAX_ATTEMPTS - otp.getAttempts();
            if (remainingAttempts > 0) {
                throw new BadRequestException("Invalid OTP code. " + remainingAttempts + " attempt(s) remaining.");
            } else {
                otpRepository.delete(otp);
                throw new BadRequestException("Invalid OTP code. Maximum attempts exceeded. Please request a new OTP.");
            }
        }

        // Mark OTP as verified
        otp.setVerified(true);
        otpRepository.save(otp);

        log.info("OTP verified successfully for email: {}", request.getEmail());

        return ApiResponse.<String>builder()
                .success(true)
                .message("OTP verified successfully")
                .data("You can now proceed with login")
                .build();
    }

    @Override
    @Transactional
    public AuthResponse loginWithOtp(LoginWithOtpRequest request) {
        // Find verified OTP
        Otp otp = otpRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("No OTP found. Please request an OTP first."));

        // Check if OTP is verified
        if (!otp.getVerified()) {
            throw new BadRequestException("OTP not verified. Please verify your OTP first.");
        }

        // Check if OTP is expired
        if (otp.isExpired()) {
            otpRepository.delete(otp);
            throw new BadRequestException("OTP has expired. Please request a new one.");
        }

        // Verify the OTP code again for security
        if (!otp.getCode().equals(request.getOtpCode())) {
            throw new BadRequestException("Invalid OTP code.");
        }

        // Load user details
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());

        // Generate JWT token
        String jwtToken = tokenProvider.generateToken(userDetails);

        // Get employee details
        Employee employee = employeeRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        // Delete the used OTP
        otpRepository.delete(otp);

        log.info("User logged in successfully with OTP: {}", request.getEmail());

        return AuthResponse.builder()
                .success(true)
                .message("Login successful with OTP")
                .token(jwtToken)
                .email(employee.getEmail())
                .role(employee.getRole().name())
                .build();
    }

    @Override
    @Transactional
    @Scheduled(fixedRate = 300000) // Run every 5 minutes
    public void cleanupExpiredOtps() {
        try {
            otpRepository.deleteByExpiresAtBefore(LocalDateTime.now());
            log.debug("Cleaned up expired OTPs");
        } catch (Exception e) {
            log.error("Error cleaning up expired OTPs", e);
        }
    }

    private String generateOtpCode() {
        SecureRandom random = new SecureRandom();
        StringBuilder otp = new StringBuilder();

        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(random.nextInt(10));
        }

        return otp.toString();
    }

    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }

        String[] parts = email.split("@");
        String localPart = parts[0];
        String domain = parts[1];

        if (localPart.length() <= 2) {
            return "**@" + domain;
        }

        String masked = localPart.charAt(0) +
                "*".repeat(localPart.length() - 2) +
                localPart.charAt(localPart.length() - 1);

        return masked + "@" + domain;
    }
}
