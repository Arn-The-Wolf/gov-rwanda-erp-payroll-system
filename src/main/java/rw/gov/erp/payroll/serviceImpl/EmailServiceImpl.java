package rw.gov.erp.payroll.serviceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import rw.gov.erp.payroll.entity.PaySlip;
import rw.gov.erp.payroll.service.EmailService;

import java.time.Month;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    
    private final JavaMailSender mailSender;
    
    @Override
    public void sendPaymentNotification(PaySlip paySlip) {
        try {
            String monthName = Month.of(paySlip.getMonth()).name();
            String message = String.format(
                    "Dear %s, Your salary of %s/%d from RCA Institution %.2f RWF has been credited to your %s account successfully.",
                    paySlip.getEmployee().getFirstName(),
                    monthName,
                    paySlip.getYear(),
                    paySlip.getNetSalary(),
                    paySlip.getEmployee().getCode()
            );
            
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(paySlip.getEmployee().getEmail());
            mailMessage.setSubject("Salary Payment Notification");
            mailMessage.setText(message);
            
            mailSender.send(mailMessage);
            log.info("Payment notification sent to: {}", paySlip.getEmployee().getEmail());
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", paySlip.getEmployee().getEmail(), e.getMessage());
        }
    }
}
