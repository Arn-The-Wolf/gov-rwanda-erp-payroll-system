package rw.gov.erp.payroll.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import rw.gov.erp.payroll.entity.Employee;
import rw.gov.erp.payroll.enums.EmployeeStatus;
import rw.gov.erp.payroll.repository.EmployeeRepository;

import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    
    private final EmployeeRepository employeeRepository;
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Loading user by email: {}", email);
        
        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        
        if (employee.getStatus() != EmployeeStatus.ACTIVE) {
            throw new UsernameNotFoundException("User account is disabled");
        }
        
        return User.builder()
                .username(employee.getEmail())
                .password(employee.getPassword())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority(employee.getRole().name())))
                .build();
    }
}
