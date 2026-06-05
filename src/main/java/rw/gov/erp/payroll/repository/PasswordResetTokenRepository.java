package rw.gov.erp.payroll.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rw.gov.erp.payroll.entity.PasswordResetToken;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByTokenAndUsedFalse(String token);
    Optional<PasswordResetToken> findByToken(String token);
    void deleteByExpiresAtBefore(LocalDateTime dateTime);
    void deleteByEmployeeId(Long employeeId);
}
