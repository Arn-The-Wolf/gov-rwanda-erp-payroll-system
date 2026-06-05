package rw.gov.erp.payroll.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rw.gov.erp.payroll.entity.Employee;
import rw.gov.erp.payroll.entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmployee(Employee employee);
    
    Optional<User> findByEmployeeEmail(String email);
    
    boolean existsByEmployee(Employee employee);
}
