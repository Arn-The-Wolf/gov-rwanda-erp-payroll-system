package rw.gov.erp.payroll.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import rw.gov.erp.payroll.enums.PaySlipStatus;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaySlipResponse {
    
    private Long id;
    
    private EmployeeResponse employee;
    
    private Double houseAmount;
    
    private Double transportAmount;
    
    private Double employeeTaxedAmount;
    
    private Double pensionAmount;
    
    private Double medicalInsuranceAmount;
    
    private Double otherTaxedAmount;
    
    private Double grossSalary;
    
    private Double netSalary;
    
    private Integer month;
    
    private Integer year;
    
    private PaySlipStatus status;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
