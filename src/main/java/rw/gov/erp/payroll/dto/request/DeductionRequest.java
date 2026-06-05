package rw.gov.erp.payroll.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import rw.gov.erp.payroll.enums.DeductionStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeductionRequest {
    
    @NotBlank(message = "Deduction name is required")
    private String deductionName;
    
    @NotNull(message = "Percentage is required")
    @Positive(message = "Percentage must be positive")
    private Double percentage;
    
    private DeductionStatus status;
}
