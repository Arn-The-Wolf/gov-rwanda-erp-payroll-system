package rw.gov.erp.payroll.service;

import rw.gov.erp.payroll.dto.request.EmploymentRequest;
import rw.gov.erp.payroll.dto.response.EmploymentResponse;

import java.util.List;

public interface EmploymentService {
    EmploymentResponse createEmployment(EmploymentRequest request);
    EmploymentResponse getEmploymentById(Long id);
    List<EmploymentResponse> getAllEmployments();
    EmploymentResponse updateEmployment(Long id, EmploymentRequest request);
    void deleteEmployment(Long id);
}
