package rw.gov.erp.payroll.service;

import rw.gov.erp.payroll.dto.response.PaySlipResponse;

import java.util.List;

public interface PaySlipService {
    List<PaySlipResponse> generatePayroll(Integer month, Integer year);
    List<PaySlipResponse> getAllPaySlips();
    PaySlipResponse getPaySlipById(Long id);
    PaySlipResponse getPaySlipByEmployeeAndPeriod(Long employeeId, Integer month, Integer year);
    PaySlipResponse approvePaySlip(Long id);
    List<PaySlipResponse> getMyPaySlips(String email);
}
