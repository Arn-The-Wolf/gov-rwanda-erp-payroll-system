package rw.gov.erp.payroll.serviceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rw.gov.erp.payroll.dto.request.EmployeeRequest;
import rw.gov.erp.payroll.dto.response.EmployeeResponse;
import rw.gov.erp.payroll.entity.Employee;
import rw.gov.erp.payroll.enums.EmployeeStatus;
import rw.gov.erp.payroll.exception.DuplicateResourceException;
import rw.gov.erp.payroll.exception.ResourceNotFoundException;
import rw.gov.erp.payroll.repository.EmployeeRepository;
import rw.gov.erp.payroll.service.EmployeeService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {
    
    private final EmployeeRepository employeeRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    @Transactional
    public EmployeeResponse createEmployee(EmployeeRequest request) {
        log.info("Creating employee with email: {}", request.getEmail());
        
        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Employee", "email", request.getEmail());
        }
        
        Employee employee = modelMapper.map(request, Employee.class);
        employee.setCode(generateEmployeeCode());
        employee.setPassword(passwordEncoder.encode(request.getPassword()));
        
        if (employee.getStatus() == null) {
            employee.setStatus(EmployeeStatus.ACTIVE);
        }
        
        Employee savedEmployee = employeeRepository.save(employee);
        log.info("Employee created successfully with code: {}", savedEmployee.getCode());
        
        return modelMapper.map(savedEmployee, EmployeeResponse.class);
    }
    
    @Override
    public EmployeeResponse getEmployeeById(Long id) {
        log.debug("Fetching employee by id: {}", id);
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));
        
        return modelMapper.map(employee, EmployeeResponse.class);
    }
    
    @Override
    public EmployeeResponse getEmployeeByEmail(String email) {
        log.debug("Fetching employee by email: {}", email);
        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "email", email));
        
        return modelMapper.map(employee, EmployeeResponse.class);
    }
    
    @Override
    public List<EmployeeResponse> getAllEmployees() {
        log.debug("Fetching all employees");
        return employeeRepository.findAll().stream()
                .map(employee -> modelMapper.map(employee, EmployeeResponse.class))
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public EmployeeResponse updateEmployee(Long id, EmployeeRequest request) {
        log.info("Updating employee with id: {}", id);
        
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));
        
        if (!employee.getEmail().equals(request.getEmail()) && 
            employeeRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Employee", "email", request.getEmail());
        }
        
        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setEmail(request.getEmail());
        employee.setDistrict(request.getDistrict());
        employee.setMobile(request.getMobile());
        employee.setDateOfBirth(request.getDateOfBirth());
        employee.setRole(request.getRole());
        
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            employee.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        
        if (request.getStatus() != null) {
            employee.setStatus(request.getStatus());
        }
        
        Employee updatedEmployee = employeeRepository.save(employee);
        log.info("Employee updated successfully with id: {}", id);
        
        return modelMapper.map(updatedEmployee, EmployeeResponse.class);
    }
    
    @Override
    @Transactional
    public void deleteEmployee(Long id) {
        log.info("Deleting employee with id: {}", id);
        
        if (!employeeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Employee", "id", id);
        }
        
        employeeRepository.deleteById(id);
        log.info("Employee deleted successfully with id: {}", id);
    }
    
    @Override
    @Transactional
    public EmployeeResponse activateEmployee(Long id) {
        log.info("Activating employee with id: {}", id);
        
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));
        
        employee.setStatus(EmployeeStatus.ACTIVE);
        Employee updatedEmployee = employeeRepository.save(employee);
        
        log.info("Employee activated successfully with id: {}", id);
        return modelMapper.map(updatedEmployee, EmployeeResponse.class);
    }
    
    @Override
    @Transactional
    public EmployeeResponse deactivateEmployee(Long id) {
        log.info("Deactivating employee with id: {}", id);
        
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));
        
        employee.setStatus(EmployeeStatus.DISABLED);
        Employee updatedEmployee = employeeRepository.save(employee);
        
        log.info("Employee deactivated successfully with id: {}", id);
        return modelMapper.map(updatedEmployee, EmployeeResponse.class);
    }
    
    private String generateEmployeeCode() {
        Integer maxNumber = employeeRepository.findMaxEmployeeCodeNumber();
        int nextNumber = (maxNumber != null ? maxNumber : 0) + 1;
        return String.format("EMP%03d", nextNumber);
    }
}
