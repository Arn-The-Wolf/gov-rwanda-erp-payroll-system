package rw.gov.erp.payroll.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    
    @Builder.Default
    private Boolean success = true;
    
    private String message;
    
    private String token;
    
    @Builder.Default
    private String type = "Bearer";
    
    private String email;
    
    private String role;
}
