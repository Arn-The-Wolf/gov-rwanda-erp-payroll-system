package rw.gov.erp.payroll.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import rw.gov.erp.payroll.enums.EmployeeStatus;
import rw.gov.erp.payroll.enums.Role;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResponse {
    
    private Long id;
    
    private String code;
    
    private String firstName;
    
    private String lastName;
    
    private String email;
    
    private String district;
    
    private String mobile;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;
    
    private Role role;
    
    private EmployeeStatus status;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
