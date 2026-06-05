package rw.gov.erp.payroll.serviceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rw.gov.erp.payroll.dto.request.DeductionRequest;
import rw.gov.erp.payroll.dto.response.DeductionResponse;
import rw.gov.erp.payroll.entity.Deduction;
import rw.gov.erp.payroll.enums.DeductionStatus;
import rw.gov.erp.payroll.exception.DuplicateResourceException;
import rw.gov.erp.payroll.exception.ResourceNotFoundException;
import rw.gov.erp.payroll.repository.DeductionRepository;
import rw.gov.erp.payroll.service.DeductionService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeductionServiceImpl implements DeductionService {
    
    private final DeductionRepository deductionRepository;
    private final ModelMapper modelMapper;
    
    @Override
    @Transactional
    public DeductionResponse createDeduction(DeductionRequest request) {
        log.info("Creating deduction: {}", request.getDeductionName());
        
        if (deductionRepository.existsByDeductionName(request.getDeductionName())) {
            throw new DuplicateResourceException("Deduction", "deductionName", request.getDeductionName());
        }
        
        Deduction deduction = modelMapper.map(request, Deduction.class);
        deduction.setCode(generateDeductionCode());
        
        if (deduction.getStatus() == null) {
            deduction.setStatus(DeductionStatus.ACTIVE);
        }
        
        Deduction savedDeduction = deductionRepository.save(deduction);
        log.info("Deduction created successfully with code: {}", savedDeduction.getCode());
        
        return modelMapper.map(savedDeduction, DeductionResponse.class);
    }
    
    @Override
    public DeductionResponse getDeductionById(Long id) {
        log.debug("Fetching deduction by id: {}", id);
        Deduction deduction = deductionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Deduction", "id", id));
        
        return modelMapper.map(deduction, DeductionResponse.class);
    }
    
    @Override
    public List<DeductionResponse> getAllDeductions() {
        log.debug("Fetching all deductions");
        return deductionRepository.findAll().stream()
                .map(deduction -> modelMapper.map(deduction, DeductionResponse.class))
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public DeductionResponse updateDeduction(Long id, DeductionRequest request) {
        log.info("Updating deduction with id: {}", id);
        
        Deduction deduction = deductionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Deduction", "id", id));
        
        if (!deduction.getDeductionName().equals(request.getDeductionName()) && 
            deductionRepository.existsByDeductionName(request.getDeductionName())) {
            throw new DuplicateResourceException("Deduction", "deductionName", request.getDeductionName());
        }
        
        deduction.setDeductionName(request.getDeductionName());
        deduction.setPercentage(request.getPercentage());
        
        if (request.getStatus() != null) {
            deduction.setStatus(request.getStatus());
        }
        
        Deduction updatedDeduction = deductionRepository.save(deduction);
        log.info("Deduction updated successfully with id: {}", id);
        
        return modelMapper.map(updatedDeduction, DeductionResponse.class);
    }
    
    @Override
    @Transactional
    public void deleteDeduction(Long id) {
        log.info("Deleting deduction with id: {}", id);
        
        if (!deductionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Deduction", "id", id);
        }
        
        deductionRepository.deleteById(id);
        log.info("Deduction deleted successfully with id: {}", id);
    }
    
    private String generateDeductionCode() {
        Integer maxNumber = deductionRepository.findMaxDeductionCodeNumber();
        int nextNumber = (maxNumber != null ? maxNumber : 0) + 1;
        return String.format("DED%03d", nextNumber);
    }
}
