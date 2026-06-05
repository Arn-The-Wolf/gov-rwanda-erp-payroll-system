package rw.gov.erp.payroll.serviceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rw.gov.erp.payroll.dto.request.EmploymentRequest;
import rw.gov.erp.payroll.dto.response.EmployeeResponse;
import rw.gov.erp.payroll.dto.response.EmploymentResponse;
import rw.gov.erp.payroll.entity.Employee;
import rw.gov.erp.payroll.entity.Employment;
import rw.gov.erp.payroll.enums.EmploymentStatus;
import rw.gov.erp.payroll.exception.ResourceNotFoundException;
import rw.gov.erp.payroll.repository.EmployeeRepository;
import rw.gov.erp.payroll.repository.EmploymentRepository;
import rw.gov.erp.payroll.service.EmploymentService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmploymentServiceImpl implements EmploymentService {
    
    private final EmploymentRepository employmentRepository;
    private final EmployeeRepository employeeRepository;
    private final ModelMapper modelMapper;
    
    @Override
    @Transactional
    public EmploymentResponse createEmployment(EmploymentRequest request) {
        log.info("Creating employment for employee id: {}", request.getEmployeeId());
        
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", request.getEmployeeId()));
        
        Employment employment = Employment.builder()
                .code(generateEmploymentCode())
                .employee(employee)
                .department(request.getDepartment())
                .position(request.getPosition())
                .baseSalary(request.getBaseSalary())
                .status(request.getStatus() != null ? request.getStatus() : EmploymentStatus.ACTIVE)
                .joiningDate(request.getJoiningDate())
                .build();
        
        Employment savedEmployment = employmentRepository.save(employment);
        log.info("Employment created successfully with code: {}", savedEmployment.getCode());
        
        return mapToResponse(savedEmployment);
    }
    
    @Override
    public EmploymentResponse getEmploymentById(Long id) {
        log.debug("Fetching employment by id: {}", id);
        Employment employment = employmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employment", "id", id));
        
        return mapToResponse(employment);
    }
    
    @Override
    public List<EmploymentResponse> getAllEmployments() {
        log.debug("Fetching all employments");
        return employmentRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public EmploymentResponse updateEmployment(Long id, EmploymentRequest request) {
        log.info("Updating employment with id: {}", id);
        
        Employment employment = employmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employment", "id", id));
        
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", request.getEmployeeId()));
        
        employment.setEmployee(employee);
        employment.setDepartment(request.getDepartment());
        employment.setPosition(request.getPosition());
        employment.setBaseSalary(request.getBaseSalary());
        employment.setJoiningDate(request.getJoiningDate());
        
        if (request.getStatus() != null) {
            employment.setStatus(request.getStatus());
        }
        
        Employment updatedEmployment = employmentRepository.save(employment);
        log.info("Employment updated successfully with id: {}", id);
        
        return mapToResponse(updatedEmployment);
    }
    
    @Override
    @Transactional
    public void deleteEmployment(Long id) {
        log.info("Deleting employment with id: {}", id);
        
        if (!employmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Employment", "id", id);
        }
        
        employmentRepository.deleteById(id);
        log.info("Employment deleted successfully with id: {}", id);
    }
    
    private String generateEmploymentCode() {
        Integer maxNumber = employmentRepository.findMaxEmploymentCodeNumber();
        int nextNumber = (maxNumber != null ? maxNumber : 0) + 1;
        return String.format("EMPLOY%03d", nextNumber);
    }
    
    private EmploymentResponse mapToResponse(Employment employment) {
        EmploymentResponse response = modelMapper.map(employment, EmploymentResponse.class);
        response.setEmployee(modelMapper.map(employment.getEmployee(), EmployeeResponse.class));
        return response;
    }
}
