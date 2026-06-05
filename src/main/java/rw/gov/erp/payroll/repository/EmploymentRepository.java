package rw.gov.erp.payroll.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import rw.gov.erp.payroll.entity.Employee;
import rw.gov.erp.payroll.entity.Employment;
import rw.gov.erp.payroll.enums.EmploymentStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmploymentRepository extends JpaRepository<Employment, Long> {
    
    Optional<Employment> findByCode(String code);
    
    boolean existsByCode(String code);
    
    List<Employment> findByEmployee(Employee employee);
    
    List<Employment> findByStatus(EmploymentStatus status);
    
    Optional<Employment> findByEmployeeAndStatus(Employee employee, EmploymentStatus status);
    
    @Query("SELECT COALESCE(MAX(CAST(SUBSTRING(e.code, 8) AS int)), 0) FROM Employment e WHERE e.code LIKE 'EMPLOY%'")
    Integer findMaxEmploymentCodeNumber();
}
