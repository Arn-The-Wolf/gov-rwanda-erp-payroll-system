-- ================================================================
-- ERP Payroll Management System - Database Setup Script
-- ================================================================
-- This script creates the PostgreSQL database for the payroll system
-- Run this script before starting the Spring Boot application
-- ================================================================

-- Step 1: Create the database
CREATE DATABASE erp_payroll_db
WITH
    ENCODING = 'UTF8'
    LC_COLLATE = 'en_US.UTF-8'
    LC_CTYPE = 'en_US.UTF-8'
    TEMPLATE = template0;

-- Step 2: Connect to the database
\c erp_payroll_db;

-- Step 3: Verify database connection
SELECT current_database() AS "Current Database";

-- ================================================================
-- NOTE: Tables will be auto-created by Spring Boot JPA
-- with spring.jpa.hibernate.ddl-auto=update
-- ================================================================

-- The following tables will be automatically generated:
-- 1. employees
-- 2. users
-- 3. employments
-- 4. deductions
-- 5. payslips
-- 6. messages

-- ================================================================
-- Verify Tables (Run after application starts)
-- ================================================================

-- Show all tables
-- SELECT table_name FROM information_schema.tables WHERE table_schema = 'public';

-- View employees table structure
-- \d employees;

-- View users table structure
-- \d users;

-- View employments table structure
-- \d employments;

-- View deductions table structure
-- \d deductions;

-- View payslips table structure
-- \d payslips;

-- View messages table structure
-- \d messages;

-- ================================================================
-- Sample Queries (Run after application has seeded data)
-- ================================================================

-- View all employees
-- SELECT * FROM employees;

-- View all deductions with percentages
-- SELECT code, deduction_name, percentage, status FROM deductions;

-- View employment details with employee names
-- SELECT e.code, CONCAT(emp.first_name, ' ', emp.last_name) AS employee_name,
--        e.department, e.position, e.base_salary, e.status
-- FROM employments e
-- JOIN employees emp ON e.employee_id = emp.id;

-- View all payslips with employee details
-- SELECT p.id, CONCAT(e.first_name, ' ', e.last_name) AS employee_name,
--        p.gross_salary, p.net_salary, p.month, p.year, p.status
-- FROM payslips p
-- JOIN employees e ON p.employee_id = e.id
-- ORDER BY p.year DESC, p.month DESC;

-- ================================================================
-- Grant Privileges (If using a specific user)
-- ================================================================

-- Create a dedicated user for the application (optional)
-- CREATE USER erp_user WITH PASSWORD 'SecurePassword123!';
-- GRANT ALL PRIVILEGES ON DATABASE erp_payroll_db TO erp_user;
-- GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO erp_user;
-- GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO erp_user;

-- ================================================================
-- END OF SCRIPT
-- ================================================================
