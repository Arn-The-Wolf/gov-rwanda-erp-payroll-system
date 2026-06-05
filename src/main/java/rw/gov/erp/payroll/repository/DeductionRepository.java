package rw.gov.erp.payroll.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import rw.gov.erp.payroll.entity.Deduction;
import rw.gov.erp.payroll.enums.DeductionStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeductionRepository extends JpaRepository<Deduction, Long> {
    
    Optional<Deduction> findByCode(String code);
    
    Optional<Deduction> findByDeductionName(String deductionName);
    
    boolean existsByCode(String code);
    
    boolean existsByDeductionName(String deductionName);
    
    List<Deduction> findByStatus(DeductionStatus status);
    
    @Query("SELECT COALESCE(MAX(CAST(SUBSTRING(d.code, 4) AS int)), 0) FROM Deduction d WHERE d.code LIKE 'DED%'")
    Integer findMaxDeductionCodeNumber();
}
