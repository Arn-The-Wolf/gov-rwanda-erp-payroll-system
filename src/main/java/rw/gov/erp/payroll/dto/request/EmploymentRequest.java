package rw.gov.erp.payroll.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import rw.gov.erp.payroll.enums.EmploymentStatus;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmploymentRequest {
    
    @NotNull(message = "Employee ID is required")
    private Long employeeId;
    
    @NotBlank(message = "Department is required")
    private String department;
    
    @NotBlank(message = "Position is required")
    private String position;
    
    @NotNull(message = "Base salary is required")
    @Positive(message = "Base salary must be positive")
    private Double baseSalary;
    
    private EmploymentStatus status;
    
    @NotNull(message = "Joining date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate joiningDate;
}
