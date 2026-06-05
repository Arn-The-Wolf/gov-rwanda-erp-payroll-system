# 🧪 API Testing Guide - Complete Request Examples

This guide provides complete JSON request examples for testing all endpoints.

## 📋 Table of Contents
1. [Authentication](#1-authentication)
2. [Employee Management](#2-employee-management)
3. [Employment Management](#3-employment-management)
4. [Deduction Management](#4-deduction-management)
5. [PaySlip Management](#5-payslip-management)

---

## 1. Authentication

### 1.1 Login (Public - No Token Required)

**Endpoint**: `POST /api/v1/auth/login`

**Manager Login**:
```json
{
  "email": "manager@erp.rw",
  "password": "Manager@123"
}
```

**Admin Login**:
```json
{
  "email": "admin@erp.rw",
  "password": "Admin@123"
}
```

**Employee Login**:
```json
{
  "email": "employee@erp.rw",
  "password": "Employee@123"
}
```

**Response Example**:
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtYW5hZ2...",
    "type": "Bearer",
    "email": "manager@erp.rw",
    "role": "ROLE_MANAGER"
  },
  "timestamp": "2025-06-02T10:30:00"
}
```

---

## 2. Employee Management

**Required Role**: MANAGER or ADMIN  
**Authorization**: Bearer Token (from login)

### 2.1 Create Employee

**Endpoint**: `POST /api/v1/employees`

**Request Body**:
```json
{
  "firstName": "Jane",
  "lastName": "Doe",
  "email": "jane.doe@erp.rw",
  "password": "Password@123",
  "mobile": "0781234567",
  "dateOfBirth": "1995-06-15",
  "role": "ROLE_EMPLOYEE",
  "status": "ACTIVE"
}
```

**Response**:
```json
{
  "success": true,
  "message": "Employee created successfully",
  "data": {
    "id": 4,
    "code": "EMP004",
    "firstName": "Jane",
    "lastName": "Doe",
    "email": "jane.doe@erp.rw",
    "mobile": "0781234567",
    "dateOfBirth": "1995-06-15",
    "role": "ROLE_EMPLOYEE",
    "status": "ACTIVE",
    "createdAt": "2025-06-02 10:35:00",
    "updatedAt": "2025-06-02 10:35:00"
  }
}
```

### 2.2 Get All Employees

**Endpoint**: `GET /api/v1/employees`

**No Request Body**

**Response**: Array of employee objects

### 2.3 Get Employee by ID

**Endpoint**: `GET /api/v1/employees/4`

**Response**: Single employee object

### 2.4 Update Employee

**Endpoint**: `PUT /api/v1/employees/4`

**Request Body**:
```json
{
  "firstName": "Jane",
  "lastName": "Smith",
  "email": "jane.smith@erp.rw",
  "password": "NewPassword@123",
  "mobile": "0781234568",
  "dateOfBirth": "1995-06-15",
  "role": "ROLE_MANAGER",
  "status": "ACTIVE"
}
```

### 2.5 Activate Employee

**Endpoint**: `PATCH /api/v1/employees/4/activate`

**No Request Body**

### 2.6 Deactivate Employee

**Endpoint**: `PATCH /api/v1/employees/4/deactivate`

**No Request Body**

### 2.7 Delete Employee

**Endpoint**: `DELETE /api/v1/employees/4`

**No Request Body**

---

## 3. Employment Management

**Required Role**: MANAGER  
**Authorization**: Bearer Token

### 3.1 Create Employment

**Endpoint**: `POST /api/v1/employment`

**Request Body**:
```json
{
  "employeeId": 3,
  "department": "Information Technology",
  "position": "Software Developer",
  "baseSalary": 70000,
  "status": "ACTIVE",
  "joiningDate": "2025-01-15"
}
```

**Response**:
```json
{
  "success": true,
  "message": "Employment created successfully",
  "data": {
    "id": 1,
    "code": "EMPLOY001",
    "employee": {
      "id": 3,
      "code": "EMP003",
      "firstName": "John",
      "lastName": "Doe",
      "email": "employee@erp.rw",
      "role": "ROLE_EMPLOYEE",
      "status": "ACTIVE"
    },
    "department": "Information Technology",
    "position": "Software Developer",
    "baseSalary": 70000.0,
    "status": "ACTIVE",
    "joiningDate": "2025-01-15",
    "createdAt": "2025-06-02 10:40:00",
    "updatedAt": "2025-06-02 10:40:00"
  }
}
```

### 3.2 Get All Employments

**Endpoint**: `GET /api/v1/employment`

### 3.3 Get Employment by ID

**Endpoint**: `GET /api/v1/employment/1`

### 3.4 Update Employment

**Endpoint**: `PUT /api/v1/employment/1`

**Request Body**:
```json
{
  "employeeId": 3,
  "department": "IT Department",
  "position": "Senior Software Developer",
  "baseSalary": 85000,
  "status": "ACTIVE",
  "joiningDate": "2025-01-15"
}
```

### 3.5 Delete Employment

**Endpoint**: `DELETE /api/v1/employment/1`

---

## 4. Deduction Management

**Required Role**: MANAGER  
**Authorization**: Bearer Token

### 4.1 Create Deduction

**Endpoint**: `POST /api/v1/deductions`

**Request Body**:
```json
{
  "deductionName": "Training",
  "percentage": 2.5,
  "status": "ACTIVE"
}
```

**Response**:
```json
{
  "success": true,
  "message": "Deduction created successfully",
  "data": {
    "id": 7,
    "code": "DED007",
    "deductionName": "Training",
    "percentage": 2.5,
    "status": "ACTIVE",
    "createdAt": "2025-06-02 10:45:00",
    "updatedAt": "2025-06-02 10:45:00"
  }
}
```

### 4.2 Get All Deductions

**Endpoint**: `GET /api/v1/deductions`

**Response**:
```json
{
  "success": true,
  "message": "Deductions retrieved successfully",
  "data": [
    {
      "id": 1,
      "code": "DED001",
      "deductionName": "EmployeeTax",
      "percentage": 30.0,
      "status": "ACTIVE"
    },
    {
      "id": 2,
      "code": "DED002",
      "deductionName": "Pension",
      "percentage": 6.0,
      "status": "ACTIVE"
    },
    {
      "id": 3,
      "code": "DED003",
      "deductionName": "MedicalInsurance",
      "percentage": 5.0,
      "status": "ACTIVE"
    },
    {
      "id": 4,
      "code": "DED004",
      "deductionName": "Others",
      "percentage": 5.0,
      "status": "ACTIVE"
    },
    {
      "id": 5,
      "code": "DED005",
      "deductionName": "Housing",
      "percentage": 14.0,
      "status": "ACTIVE"
    },
    {
      "id": 6,
      "code": "DED006",
      "deductionName": "Transport",
      "percentage": 14.0,
      "status": "ACTIVE"
    }
  ]
}
```

### 4.3 Get Deduction by ID

**Endpoint**: `GET /api/v1/deductions/2`

### 4.4 Update Deduction

**Endpoint**: `PUT /api/v1/deductions/2`

**Request Body**:
```json
{
  "deductionName": "Pension",
  "percentage": 7.0,
  "status": "ACTIVE"
}
```

### 4.5 Delete Deduction

**Endpoint**: `DELETE /api/v1/deductions/7`

---

## 5. PaySlip Management

### 5.1 Generate Payroll (MANAGER Only)

**Endpoint**: `POST /api/v1/payslips/generate/{month}/{year}`

**Example**: `POST /api/v1/payslips/generate/6/2025`

**No Request Body**

**Response**:
```json
{
  "success": true,
  "message": "Payroll generated successfully for 6/2025",
  "data": [
    {
      "id": 1,
      "employee": {
        "id": 3,
        "code": "EMP003",
        "firstName": "John",
        "lastName": "Doe",
        "email": "employee@erp.rw",
        "role": "ROLE_EMPLOYEE",
        "status": "ACTIVE"
      },
      "houseAmount": 9800.0,
      "transportAmount": 9800.0,
      "employeeTaxedAmount": 21000.0,
      "pensionAmount": 4200.0,
      "medicalInsuranceAmount": 3500.0,
      "otherTaxedAmount": 3500.0,
      "grossSalary": 89600.0,
      "netSalary": 57400.0,
      "month": 6,
      "year": 2025,
      "status": "PENDING",
      "createdAt": "2025-06-02 10:50:00",
      "updatedAt": "2025-06-02 10:50:00"
    }
  ]
}
```

**Calculation Breakdown for baseSalary = 70,000**:
```
houseAmount = 70,000 × 14% = 9,800
transportAmount = 70,000 × 14% = 9,800
grossSalary = 70,000 + 9,800 + 9,800 = 89,600

employeeTax = 70,000 × 30% = 21,000
pension = 70,000 × 6% = 4,200
medicalInsurance = 70,000 × 5% = 3,500
others = 70,000 × 5% = 3,500

netSalary = 89,600 - (21,000 + 4,200 + 3,500 + 3,500) = 57,400
```

### 5.2 Get All PaySlips (Role-Based)

**Endpoint**: `GET /api/v1/payslips`

**Behavior**:
- **MANAGER/ADMIN**: Returns all payslips
- **EMPLOYEE**: Returns only their own payslips

**Response**: Array of payslip objects

### 5.3 Get PaySlip by Employee and Period (MANAGER/ADMIN Only)

**Endpoint**: `GET /api/v1/payslips/{employeeId}/{month}/{year}`

**Example**: `GET /api/v1/payslips/3/6/2025`

**Response**: Single payslip object

### 5.4 Approve PaySlip (ADMIN Only)

**Endpoint**: `PATCH /api/v1/payslips/{id}/approve`

**Example**: `PATCH /api/v1/payslips/1/approve`

**No Request Body**

**Response**:
```json
{
  "success": true,
  "message": "PaySlip approved successfully",
  "data": {
    "id": 1,
    "employee": {
      "id": 3,
      "code": "EMP003",
      "firstName": "John",
      "lastName": "Doe",
      "email": "employee@erp.rw"
    },
    "houseAmount": 9800.0,
    "transportAmount": 9800.0,
    "employeeTaxedAmount": 21000.0,
    "pensionAmount": 4200.0,
    "medicalInsuranceAmount": 3500.0,
    "otherTaxedAmount": 3500.0,
    "grossSalary": 89600.0,
    "netSalary": 57400.0,
    "month": 6,
    "year": 2025,
    "status": "PAID",
    "createdAt": "2025-06-02 10:50:00",
    "updatedAt": "2025-06-02 10:55:00"
  }
}
```

**Email Sent**: After approval, email is automatically sent to employee@erp.rw

---

## 🔄 Complete Testing Workflow

### Scenario: Manager Generates Payroll, Admin Approves

#### Step 1: Login as Manager
```
POST /api/v1/auth/login
Body: { "email": "manager@erp.rw", "password": "Manager@123" }
Copy the token
```

#### Step 2: Authorize in Swagger
```
Click 🔒 Authorize
Enter: Bearer YOUR_TOKEN_HERE
```

#### Step 3: Verify Deductions
```
GET /api/v1/deductions
Confirm all 6 deductions exist with correct percentages
```

#### Step 4: Create Employment (if not exists)
```
POST /api/v1/employment
Body: {
  "employeeId": 3,
  "department": "IT",
  "position": "Developer",
  "baseSalary": 70000,
  "status": "ACTIVE",
  "joiningDate": "2025-01-01"
}
Note the employment ID
```

#### Step 5: Generate Payroll
```
POST /api/v1/payslips/generate/6/2025
Verify response shows PENDING status
Note the payslip ID
```

#### Step 6: View Generated Payslips
```
GET /api/v1/payslips
Verify calculations are correct
```

#### Step 7: Login as Admin
```
POST /api/v1/auth/login
Body: { "email": "admin@erp.rw", "password": "Admin@123" }
Copy the new token
Re-authorize in Swagger with new token
```

#### Step 8: Approve Payslip
```
PATCH /api/v1/payslips/1/approve
Verify status changed to PAID
Check email (if configured)
```

#### Step 9: Login as Employee
```
POST /api/v1/auth/login
Body: { "email": "employee@erp.rw", "password": "Employee@123" }
Authorize with employee token
```

#### Step 10: View Own Payslip
```
GET /api/v1/payslips
Employee should only see their own payslips
```

---

## ❌ Error Response Examples

### 401 Unauthorized (No Token)
```json
{
  "success": false,
  "message": "Unauthorized: Full authentication is required",
  "timestamp": "2025-06-02T10:00:00"
}
```

### 403 Forbidden (Wrong Role)
```json
{
  "success": false,
  "message": "Access denied: Access Denied",
  "timestamp": "2025-06-02T10:00:00"
}
```

### 404 Not Found
```json
{
  "success": false,
  "message": "Employee not found with id: '999'",
  "timestamp": "2025-06-02T10:00:00"
}
```

### 409 Conflict (Duplicate)
```json
{
  "success": false,
  "message": "PaySlip already exists for employee EMP003 for period 6/2025",
  "timestamp": "2025-06-02T10:00:00"
}
```

### 400 Bad Request (Validation)
```json
{
  "success": false,
  "message": "Validation failed",
  "data": {
    "email": "Email must be valid",
    "baseSalary": "Base salary must be positive"
  },
  "timestamp": "2025-06-02T10:00:00"
}
```

---

## 🎯 Testing Tips

1. **Always authorize after login** - Token expires after 24 hours
2. **Create employment before payroll** - Employee must have active employment
3. **Check HTTP status codes** - 200/201 = success, 4xx = client error, 5xx = server error
4. **Can't generate twice** - Unique constraint prevents duplicate payroll for same month/year
5. **Role matters** - EMPLOYEE can't approve payslips, only ADMIN can
6. **Decimal precision** - Amounts are rounded to 2 decimal places

---

## 📊 Sample Test Data

### Employees (Pre-loaded)
1. **EMP001** - admin@erp.rw (ADMIN)
2. **EMP002** - manager@erp.rw (MANAGER)
3. **EMP003** - employee@erp.rw (EMPLOYEE)

### Deductions (Pre-loaded)
- DED001: EmployeeTax (30%)
- DED002: Pension (6%)
- DED003: MedicalInsurance (5%)
- DED004: Others (5%)
- DED005: Housing (14%)
- DED006: Transport (14%)

### Test Salaries
- 70,000 RWF → Net: 57,400 RWF
- 50,000 RWF → Net: 41,000 RWF
- 100,000 RWF → Net: 82,000 RWF

---

**Happy Testing! 🚀**
