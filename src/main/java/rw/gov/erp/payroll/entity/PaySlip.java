package rw.gov.erp.payroll.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import rw.gov.erp.payroll.enums.PaySlipStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "payslips", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"employee_id", "month", "year"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class PaySlip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(nullable = false)
    private Double houseAmount;

    @Column(nullable = false)
    private Double transportAmount;

    @Column(nullable = false)
    private Double employeeTaxedAmount;

    @Column(nullable = false)
    private Double pensionAmount;

    @Column(nullable = false)
    private Double medicalInsuranceAmount;

    @Column(nullable = false)
    private Double otherTaxedAmount;

    @Column(nullable = false)
    private Double grossSalary;

    @Column(nullable = false)
    private Double netSalary;

    @Column(nullable = false)
    private Integer month;

    @Column(nullable = false)
    private Integer year;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private PaySlipStatus status = PaySlipStatus.PENDING;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
