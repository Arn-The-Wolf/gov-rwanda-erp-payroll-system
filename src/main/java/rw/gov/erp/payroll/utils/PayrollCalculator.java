package rw.gov.erp.payroll.utils;

import lombok.experimental.UtilityClass;
import rw.gov.erp.payroll.entity.Deduction;
import rw.gov.erp.payroll.exception.BadRequestException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@UtilityClass
public class PayrollCalculator {
    
    public static Map<String, Double> calculateSalary(Double baseSalary, List<Deduction> deductions) {
        if (baseSalary == null || baseSalary <= 0) {
            throw new BadRequestException("Base salary must be positive");
        }
        
        Map<String, Double> calculations = new HashMap<>();
        
        // Get deduction percentages
        double housingPercentage = getDeductionPercentage(deductions, "House");
        double transportPercentage = getDeductionPercentage(deductions, "Transport");
        double employeeTaxPercentage = getDeductionPercentage(deductions, "EmployeeTax");
        double pensionPercentage = getDeductionPercentage(deductions, "Pansion");
        double medicalInsurancePercentage = getDeductionPercentage(deductions, "MedicalInsurance");
        double othersPercentage = getDeductionPercentage(deductions, "Others");
        
        // Calculate amounts
        double houseAmount = baseSalary * housingPercentage / 100;
        double transportAmount = baseSalary * transportPercentage / 100;
        double grossSalary = baseSalary + houseAmount + transportAmount;
        
        double employeeTaxedAmount = baseSalary * employeeTaxPercentage / 100;
        double pensionAmount = baseSalary * pensionPercentage / 100;
        double medicalInsuranceAmount = baseSalary * medicalInsurancePercentage / 100;
        double otherTaxedAmount = baseSalary * othersPercentage / 100;
        
        double totalDeductions = employeeTaxedAmount + pensionAmount + medicalInsuranceAmount + otherTaxedAmount;
        
        // Validate deductions don't exceed gross salary
        if (totalDeductions > grossSalary) {
            throw new BadRequestException(
                String.format("Total deductions (%.2f) cannot exceed gross salary (%.2f)", 
                    totalDeductions, grossSalary)
            );
        }
        
        // netSalary = baseSalary - deductions (NOT grossSalary - deductions)
        double netSalary = baseSalary - totalDeductions;
        
        // Store all calculations
        calculations.put("houseAmount", roundToTwoDecimals(houseAmount));
        calculations.put("transportAmount", roundToTwoDecimals(transportAmount));
        calculations.put("grossSalary", roundToTwoDecimals(grossSalary));
        calculations.put("employeeTaxedAmount", roundToTwoDecimals(employeeTaxedAmount));
        calculations.put("pensionAmount", roundToTwoDecimals(pensionAmount));
        calculations.put("medicalInsuranceAmount", roundToTwoDecimals(medicalInsuranceAmount));
        calculations.put("otherTaxedAmount", roundToTwoDecimals(otherTaxedAmount));
        calculations.put("netSalary", roundToTwoDecimals(netSalary));
        
        return calculations;
    }
    
    private static double getDeductionPercentage(List<Deduction> deductions, String deductionName) {
        return deductions.stream()
                .filter(d -> d.getDeductionName().equalsIgnoreCase(deductionName))
                .findFirst()
                .map(Deduction::getPercentage)
                .orElse(0.0);
    }
    
    private static double roundToTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
