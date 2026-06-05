package rw.gov.erp.payroll.service;

import rw.gov.erp.payroll.entity.PaySlip;

public interface EmailService {
    void sendPaymentNotification(PaySlip paySlip);
    void sendEmail(String to, String subject, String body);
}
