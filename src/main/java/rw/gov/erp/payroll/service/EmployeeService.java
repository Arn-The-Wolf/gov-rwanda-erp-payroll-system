package rw.gov.erp.payroll.service;

import rw.gov.erp.payroll.dto.request.EmployeeRequest;
import rw.gov.erp.payroll.dto.response.EmployeeResponse;

import java.util.List;

public interface EmployeeService {
    EmployeeResponse createEmployee(EmployeeRequest request);
    EmployeeResponse getEmployeeById(Long id);
    EmployeeResponse getEmployeeByEmail(String email);
    List<EmployeeResponse> getAllEmployees();
    EmployeeResponse updateEmployee(Long id, EmployeeRequest request);
    void deleteEmployee(Long id);
    EmployeeResponse activateEmployee(Long id);
    EmployeeResponse deactivateEmployee(Long id);
}
