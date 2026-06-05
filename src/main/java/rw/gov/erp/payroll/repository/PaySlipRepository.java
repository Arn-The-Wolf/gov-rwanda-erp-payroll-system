package rw.gov.erp.payroll.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rw.gov.erp.payroll.entity.Employee;
import rw.gov.erp.payroll.entity.PaySlip;
import rw.gov.erp.payroll.enums.PaySlipStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaySlipRepository extends JpaRepository<PaySlip, Long> {
    
    Optional<PaySlip> findByEmployeeAndMonthAndYear(Employee employee, Integer month, Integer year);
    
    boolean existsByEmployeeAndMonthAndYear(Employee employee, Integer month, Integer year);
    
    List<PaySlip> findByMonthAndYear(Integer month, Integer year);
    
    List<PaySlip> findByEmployee(Employee employee);
    
    List<PaySlip> findByStatus(PaySlipStatus status);
    
    List<PaySlip> findByMonthAndYearAndStatus(Integer month, Integer year, PaySlipStatus status);
}
