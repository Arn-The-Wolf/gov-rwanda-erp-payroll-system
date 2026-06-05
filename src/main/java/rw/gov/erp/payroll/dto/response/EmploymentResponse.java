package rw.gov.erp.payroll.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import rw.gov.erp.payroll.enums.EmploymentStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmploymentResponse {
    
    private Long id;
    
    private String code;
    
    private EmployeeResponse employee;
    
    private String department;
    
    private String position;
    
    private Double baseSalary;
    
    private EmploymentStatus status;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate joiningDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
