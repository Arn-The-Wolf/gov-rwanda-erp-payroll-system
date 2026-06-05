package rw.gov.erp.payroll.service;

import rw.gov.erp.payroll.dto.request.LoginRequest;
import rw.gov.erp.payroll.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse login(LoginRequest loginRequest);
}
