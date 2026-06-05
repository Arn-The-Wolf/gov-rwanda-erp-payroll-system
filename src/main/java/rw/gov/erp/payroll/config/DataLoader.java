package rw.gov.erp.payroll.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import rw.gov.erp.payroll.entity.Deduction;
import rw.gov.erp.payroll.entity.Employee;
import rw.gov.erp.payroll.enums.DeductionStatus;
import rw.gov.erp.payroll.enums.EmployeeStatus;
import rw.gov.erp.payroll.enums.Role;
import rw.gov.erp.payroll.repository.DeductionRepository;
import rw.gov.erp.payroll.repository.EmployeeRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {
    
    private final DeductionRepository deductionRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        loadDeductions();
        loadDefaultUsers();
    }
    
    private void loadDeductions() {
        if (deductionRepository.count() > 0) {
            log.info("Deductions already loaded, skipping...");
            return;
        }
        
        log.info("Loading default deductions...");
        
        List<Deduction> deductions = new ArrayList<>();
        
        deductions.add(Deduction.builder()
                .code("DED001")
                .deductionName("EmployeeTax")
                .percentage(30.0)
                .status(DeductionStatus.ACTIVE)
                .build());
        
        deductions.add(Deduction.builder()
                .code("DED002")
                .deductionName("Pansion")
                .percentage(6.0)
                .status(DeductionStatus.ACTIVE)
                .build());
        
        deductions.add(Deduction.builder()
                .code("DED003")
                .deductionName("MedicalInsurance")
                .percentage(5.0)
                .status(DeductionStatus.ACTIVE)
                .build());
        
        deductions.add(Deduction.builder()
                .code("DED004")
                .deductionName("Others")
                .percentage(5.0)
                .status(DeductionStatus.ACTIVE)
                .build());
        
        deductions.add(Deduction.builder()
                .code("DED005")
                .deductionName("House")
                .percentage(14.0)
                .status(DeductionStatus.ACTIVE)
                .build());
        
        deductions.add(Deduction.builder()
                .code("DED006")
                .deductionName("Transport")
                .percentage(14.0)
                .status(DeductionStatus.ACTIVE)
                .build());
        
        deductionRepository.saveAll(deductions);
        log.info("Loaded {} deductions successfully", deductions.size());
    }
    
    private void loadDefaultUsers() {
        if (employeeRepository.count() > 0) {
            log.info("Default users already loaded, skipping...");
            return;
        }
        
        log.info("Loading default users...");
        
        List<Employee> employees = new ArrayList<>();
        
        // Create ADMIN user
        employees.add(Employee.builder()
                .code("EMP001")
                .firstName("System")
                .lastName("Admin")
                .email("admin@erp.rw")
                .password(passwordEncoder.encode("Admin@123"))
                .district("Kigali")
                .mobile("0781234567")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .role(Role.ROLE_ADMIN)
                .status(EmployeeStatus.ACTIVE)
                .build());
        
        // Create MANAGER user
        employees.add(Employee.builder()
                .code("EMP002")
                .firstName("System")
                .lastName("Manager")
                .email("manager@erp.rw")
                .password(passwordEncoder.encode("Manager@123"))
                .district("Kigali")
                .mobile("0781234568")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .role(Role.ROLE_MANAGER)
                .status(EmployeeStatus.ACTIVE)
                .build());
        
        // Create EMPLOYEE user for testing
        employees.add(Employee.builder()
                .code("EMP003")
                .firstName("John")
                .lastName("Doe")
                .email("employee@erp.rw")
                .password(passwordEncoder.encode("Employee@123"))
                .district("Kigali")
                .mobile("0781234569")
                .dateOfBirth(LocalDate.of(1995, 5, 15))
                .role(Role.ROLE_EMPLOYEE)
                .status(EmployeeStatus.ACTIVE)
                .build());
        
        employeeRepository.saveAll(employees);
        log.info("Loaded {} default users successfully", employees.size());
        log.info("Default credentials:");
        log.info("Admin: admin@erp.rw / Admin@123");
        log.info("Manager: manager@erp.rw / Manager@123");
        log.info("Employee: employee@erp.rw / Employee@123");
    }
}
