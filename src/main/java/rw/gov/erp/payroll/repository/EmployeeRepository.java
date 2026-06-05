package rw.gov.erp.payroll.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import rw.gov.erp.payroll.entity.Employee;
import rw.gov.erp.payroll.enums.EmployeeStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    
    Optional<Employee> findByEmail(String email);
    
    Optional<Employee> findByCode(String code);
    
    boolean existsByEmail(String email);
    
    boolean existsByCode(String code);
    
    List<Employee> findByStatus(EmployeeStatus status);
    
    @Query("SELECT COALESCE(MAX(CAST(SUBSTRING(e.code, 4) AS int)), 0) FROM Employee e WHERE e.code LIKE 'EMP%'")
    Integer findMaxEmployeeCodeNumber();
}
