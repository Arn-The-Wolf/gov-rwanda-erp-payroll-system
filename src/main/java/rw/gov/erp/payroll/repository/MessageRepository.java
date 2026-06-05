package rw.gov.erp.payroll.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rw.gov.erp.payroll.entity.Employee;
import rw.gov.erp.payroll.entity.Message;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    
    List<Message> findByEmployee(Employee employee);
    
    List<Message> findByMonthAndYear(Integer month, Integer year);
}
