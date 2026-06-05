package rw.gov.erp.payroll.serviceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import rw.gov.erp.payroll.dto.request.LoginRequest;
import rw.gov.erp.payroll.dto.response.AuthResponse;
import rw.gov.erp.payroll.entity.Employee;
import rw.gov.erp.payroll.repository.EmployeeRepository;
import rw.gov.erp.payroll.security.JwtTokenProvider;
import rw.gov.erp.payroll.service.AuthService;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmployeeRepository employeeRepository;
    
    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        log.info("Login attempt for email: {}", loginRequest.getEmail());
        
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );
        
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtTokenProvider.generateToken(userDetails);
        
        Employee employee = employeeRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow();
        
        log.info("Login successful for email: {}", loginRequest.getEmail());
        
        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .email(employee.getEmail())
                .role(employee.getRole().name())
                .build();
    }
}
