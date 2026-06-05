package rw.gov.erp.payroll.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rw.gov.erp.payroll.entity.Otp;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<Otp, Long> {
    Optional<Otp> findByEmailAndVerifiedFalse(String email);
    Optional<Otp> findByEmail(String email);
    void deleteByExpiresAtBefore(LocalDateTime dateTime);
    void deleteByEmail(String email);
}
