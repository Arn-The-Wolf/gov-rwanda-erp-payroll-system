-- ================================================================
-- Task 6: Database Routines - Triggers, Functions, and Cursors
-- ================================================================
-- PostgreSQL Version
-- This script implements database-level messaging when payslips are approved
-- Run this after the application has created the tables
-- ================================================================

-- Connect to the database
\c erp_payroll_db;

-- ================================================================
-- Drop existing objects if they exist
-- ================================================================
DROP TRIGGER IF EXISTS after_payslip_status_update ON payslips;
DROP FUNCTION IF EXISTS send_payslip_message_trigger();
DROP FUNCTION IF EXISTS send_payslip_message(BIGINT);
DROP FUNCTION IF EXISTS process_all_approved_payslips();

-- ================================================================
-- FUNCTION: send_payslip_message
-- Purpose: Generate and store message for approved payslip
-- ================================================================
CREATE OR REPLACE FUNCTION send_payslip_message(p_payslip_id BIGINT)
RETURNS VOID AS $$
DECLARE
    v_employee_id BIGINT;
    v_first_name VARCHAR(100);
    v_employee_code VARCHAR(50);
    v_net_salary DECIMAL(10,2);
    v_month INT;
    v_year INT;
    v_message_text TEXT;
    v_month_year VARCHAR(20);
    v_month_name VARCHAR(20);
BEGIN
    -- Get payslip details
    SELECT 
        ps.employee_id,
        e.first_name,
        e.code,
        ps.net_salary,
        ps.month,
        ps.year
    INTO
        v_employee_id,
        v_first_name,
        v_employee_code,
        v_net_salary,
        v_month,
        v_year
    FROM payslips ps
    INNER JOIN employees e ON ps.employee_id = e.id
    WHERE ps.id = p_payslip_id;
    
    -- Convert month number to name
    v_month_name := CASE v_month
        WHEN 1 THEN 'JANUARY'
        WHEN 2 THEN 'FEBRUARY'
        WHEN 3 THEN 'MARCH'
        WHEN 4 THEN 'APRIL'
        WHEN 5 THEN 'MAY'
        WHEN 6 THEN 'JUNE'
        WHEN 7 THEN 'JULY'
        WHEN 8 THEN 'AUGUST'
        WHEN 9 THEN 'SEPTEMBER'
        WHEN 10 THEN 'OCTOBER'
        WHEN 11 THEN 'NOVEMBER'
        WHEN 12 THEN 'DECEMBER'
    END;
    
    -- Create month-year string
    v_month_year := LPAD(v_month::TEXT, 2, '0') || '-' || v_year::TEXT;
    
    -- Generate message in the required format
    v_message_text := 'Dear ' || v_first_name || 
        ' Your salary of ' || v_month_name || '/' || v_year || 
        ' from RCA Institution ' || TO_CHAR(v_net_salary, 'FM999999999.00') || 
        ' RWF has been credited to your ' || v_employee_code || 
        ' account Successfully.';
    
    -- Insert message into messages table
    INSERT INTO messages (employee_id, message, month, year, month_year, sent_at)
    VALUES (v_employee_id, v_message_text, v_month, v_year, v_month_year, CURRENT_TIMESTAMP);
    
END;
$$ LANGUAGE plpgsql;

-- ================================================================
-- FUNCTION: process_all_approved_payslips (Using Cursor)
-- Purpose: Process all pending payslips that need messages
-- This demonstrates cursor usage as required
-- ================================================================
CREATE OR REPLACE FUNCTION process_all_approved_payslips()
RETURNS VOID AS $$
DECLARE
    v_payslip_id BIGINT;
    v_status VARCHAR(20);
    
    -- Declare cursor for approved payslips without messages
    payslip_cursor CURSOR FOR
        SELECT ps.id, ps.status
        FROM payslips ps
        WHERE ps.status = 'PAID'
        AND NOT EXISTS (
            SELECT 1 FROM messages m 
            WHERE m.employee_id = ps.employee_id 
            AND m.month = ps.month 
            AND m.year = ps.year
        );
BEGIN
    -- Open cursor and loop through results
    FOR v_payslip_id, v_status IN payslip_cursor LOOP
        -- Send message for this payslip
        PERFORM send_payslip_message(v_payslip_id);
    END LOOP;
END;
$$ LANGUAGE plpgsql;

-- ================================================================
-- TRIGGER FUNCTION: send_payslip_message_trigger
-- Purpose: Trigger function to be called after payslip update
-- ================================================================
CREATE OR REPLACE FUNCTION send_payslip_message_trigger()
RETURNS TRIGGER AS $$
BEGIN
    -- Check if status changed from PENDING to PAID
    IF OLD.status = 'PENDING' AND NEW.status = 'PAID' THEN
        -- Check if message doesn't already exist
        IF NOT EXISTS (
            SELECT 1 FROM messages 
            WHERE employee_id = NEW.employee_id 
            AND month = NEW.month 
            AND year = NEW.year
        ) THEN
            -- Send the message
            PERFORM send_payslip_message(NEW.id);
        END IF;
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- ================================================================
-- TRIGGER: after_payslip_status_update
-- Purpose: Automatically send message when payslip status changes to PAID
-- This is triggered when ADMIN approves the payslip
-- ================================================================
CREATE TRIGGER after_payslip_status_update
AFTER UPDATE ON payslips
FOR EACH ROW
EXECUTE FUNCTION send_payslip_message_trigger();

-- ================================================================
-- Verification Queries
-- ================================================================

-- View all triggers
-- SELECT trigger_name, event_object_table, action_statement 
-- FROM information_schema.triggers 
-- WHERE trigger_schema = 'public';

-- View all functions
-- SELECT routine_name, routine_type 
-- FROM information_schema.routines 
-- WHERE routine_schema = 'public';

-- Test the trigger (Run after application generates payslips)
-- UPDATE payslips SET status = 'PAID' WHERE id = 1;

-- Check messages generated
-- SELECT * FROM messages;

-- Test cursor function
-- SELECT process_all_approved_payslips();

-- ================================================================
-- Example: Manual testing
-- ================================================================

-- 1. Generate payslip via API (Manager role)
-- POST /api/v1/payslips/generate/6/2025

-- 2. Approve payslip via API (Admin role) - This will trigger the database routine
-- PATCH /api/v1/payslips/1/approve

-- 3. Check messages table
-- SELECT 
--     m.id,
--     e.first_name,
--     e.code AS employee_code,
--     m.message,
--     m.month_year,
--     m.sent_at
-- FROM messages m
-- INNER JOIN employees e ON m.employee_id = e.id
-- ORDER BY m.sent_at DESC;

-- ================================================================
-- Additional PostgreSQL-specific verification
-- ================================================================

-- Check if functions exist
-- \df send_payslip_message
-- \df process_all_approved_payslips
-- \df send_payslip_message_trigger

-- Check if trigger exists
-- \dS payslips

-- View function definitions
-- \sf send_payslip_message
-- \sf process_all_approved_payslips

-- ================================================================
-- END OF DATABASE ROUTINES SCRIPT
-- ================================================================
