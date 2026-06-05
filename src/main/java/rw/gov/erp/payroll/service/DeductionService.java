package rw.gov.erp.payroll.service;

import rw.gov.erp.payroll.dto.request.DeductionRequest;
import rw.gov.erp.payroll.dto.response.DeductionResponse;

import java.util.List;

public interface DeductionService {
    DeductionResponse createDeduction(DeductionRequest request);
    DeductionResponse getDeductionById(Long id);
    List<DeductionResponse> getAllDeductions();
    DeductionResponse updateDeduction(Long id, DeductionRequest request);
    void deleteDeduction(Long id);
}
