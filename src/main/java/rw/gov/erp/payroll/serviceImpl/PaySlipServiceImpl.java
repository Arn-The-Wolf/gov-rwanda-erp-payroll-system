package rw.gov.erp.payroll.serviceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rw.gov.erp.payroll.dto.response.EmployeeResponse;
import rw.gov.erp.payroll.dto.response.PaySlipResponse;
import rw.gov.erp.payroll.entity.Deduction;
import rw.gov.erp.payroll.entity.Employee;
import rw.gov.erp.payroll.entity.Employment;
import rw.gov.erp.payroll.entity.Message;
import rw.gov.erp.payroll.entity.PaySlip;
import rw.gov.erp.payroll.enums.DeductionStatus;
import rw.gov.erp.payroll.enums.EmployeeStatus;
import rw.gov.erp.payroll.enums.EmploymentStatus;
import rw.gov.erp.payroll.enums.PaySlipStatus;
import rw.gov.erp.payroll.exception.BadRequestException;
import rw.gov.erp.payroll.exception.DuplicateResourceException;
import rw.gov.erp.payroll.exception.ResourceNotFoundException;
import rw.gov.erp.payroll.repository.DeductionRepository;
import rw.gov.erp.payroll.repository.EmployeeRepository;
import rw.gov.erp.payroll.repository.EmploymentRepository;
import rw.gov.erp.payroll.repository.MessageRepository;
import rw.gov.erp.payroll.repository.PaySlipRepository;
import rw.gov.erp.payroll.service.EmailService;
import rw.gov.erp.payroll.service.PaySlipService;
import rw.gov.erp.payroll.utils.PayrollCalculator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaySlipServiceImpl implements PaySlipService {
    
    private final PaySlipRepository paySlipRepository;
    private final EmployeeRepository employeeRepository;
    private final EmploymentRepository employmentRepository;
    private final DeductionRepository deductionRepository;
    private final MessageRepository messageRepository;
    private final EmailService emailService;
    private final ModelMapper modelMapper;
    
    @Override
    @Transactional
    @PreAuthorize("hasRole('MANAGER')")
    public List<PaySlipResponse> generatePayroll(Integer month, Integer year) {
        log.info("Generating payroll for month: {}, year: {}", month, year);
        
        if (month < 1 || month > 12) {
            throw new BadRequestException("Month must be between 1 and 12");
        }
        
        // Get all active deductions
        List<Deduction> deductions = deductionRepository.findByStatus(DeductionStatus.ACTIVE);
        if (deductions.isEmpty()) {
            throw new BadRequestException("No active deductions found. Please configure deductions first.");
        }
        
        // Get all active employees
        List<Employee> activeEmployees = employeeRepository.findByStatus(EmployeeStatus.ACTIVE);
        if (activeEmployees.isEmpty()) {
            throw new BadRequestException("No active employees found");
        }
        
        List<PaySlip> paySlips = new ArrayList<>();
        
        for (Employee employee : activeEmployees) {
            // Check if payslip already exists for this employee in this period
            if (paySlipRepository.existsByEmployeeAndMonthAndYear(employee, month, year)) {
                log.warn("PaySlip already exists for employee {} for {}/{}", 
                        employee.getCode(), month, year);
                throw new DuplicateResourceException(
                        String.format("PaySlip already exists for employee %s for period %d/%d", 
                                employee.getCode(), month, year)
                );
            }
            
            // Get active employment for this employee
            Employment employment = employmentRepository
                    .findByEmployeeAndStatus(employee, EmploymentStatus.ACTIVE)
                    .orElse(null);
            
            if (employment == null) {
                log.warn("No active employment found for employee: {}", employee.getCode());
                continue;
            }
            
            // Calculate salary using PayrollCalculator
            Map<String, Double> calculations = PayrollCalculator.calculateSalary(
                    employment.getBaseSalary(), 
                    deductions
            );
            
            // Create payslip
            PaySlip paySlip = PaySlip.builder()
                    .employee(employee)
                    .houseAmount(calculations.get("houseAmount"))
                    .transportAmount(calculations.get("transportAmount"))
                    .grossSalary(calculations.get("grossSalary"))
                    .employeeTaxedAmount(calculations.get("employeeTaxedAmount"))
                    .pensionAmount(calculations.get("pensionAmount"))
                    .medicalInsuranceAmount(calculations.get("medicalInsuranceAmount"))
                    .otherTaxedAmount(calculations.get("otherTaxedAmount"))
                    .netSalary(calculations.get("netSalary"))
                    .month(month)
                    .year(year)
                    .status(PaySlipStatus.PENDING)
                    .build();
            
            paySlips.add(paySlip);
        }
        
        List<PaySlip> savedPaySlips = paySlipRepository.saveAll(paySlips);
        log.info("Generated {} payslips for {}/{}", savedPaySlips.size(), month, year);
        
        return savedPaySlips.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<PaySlipResponse> getAllPaySlips() {
        log.debug("Fetching all payslips");
        return paySlipRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public PaySlipResponse getPaySlipById(Long id) {
        log.debug("Fetching payslip by id: {}", id);
        PaySlip paySlip = paySlipRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PaySlip", "id", id));
        
        return mapToResponse(paySlip);
    }
    
    @Override
    public PaySlipResponse getPaySlipByEmployeeAndPeriod(Long employeeId, Integer month, Integer year) {
        log.debug("Fetching payslip for employee: {}, month: {}, year: {}", employeeId, month, year);
        
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", employeeId));
        
        PaySlip paySlip = paySlipRepository.findByEmployeeAndMonthAndYear(employee, month, year)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("PaySlip not found for employee %s for period %d/%d", 
                                employee.getCode(), month, year)
                ));
        
        return mapToResponse(paySlip);
    }
    
    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public PaySlipResponse approvePaySlip(Long id) {
        log.info("Approving payslip with id: {}", id);
        
        PaySlip paySlip = paySlipRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PaySlip", "id", id));
        
        if (paySlip.getStatus() == PaySlipStatus.PAID) {
            throw new BadRequestException("PaySlip is already approved");
        }
        
        paySlip.setStatus(PaySlipStatus.PAID);
        PaySlip approvedPaySlip = paySlipRepository.save(paySlip);
        
        // Note: Database trigger will automatically create message in messages table
        // when status changes from PENDING to PAID
        
        // Send email notification
        try {
            emailService.sendPaymentNotification(approvedPaySlip);
            log.info("Email notification sent for payslip id: {}", id);
        } catch (Exception e) {
            log.error("Failed to send email notification for payslip id: {}", id, e);
        }
        
        log.info("PaySlip approved successfully with id: {} - Database trigger will generate message", id);
        return mapToResponse(approvedPaySlip);
    }
    
    @Override
    public List<PaySlipResponse> getMyPaySlips(String email) {
        log.debug("Fetching payslips for employee email: {}", email);
        
        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "email", email));
        
        return paySlipRepository.findByEmployee(employee).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    private PaySlipResponse mapToResponse(PaySlip paySlip) {
        PaySlipResponse response = modelMapper.map(paySlip, PaySlipResponse.class);
        response.setEmployee(modelMapper.map(paySlip.getEmployee(), EmployeeResponse.class));
        return response;
    }
}
