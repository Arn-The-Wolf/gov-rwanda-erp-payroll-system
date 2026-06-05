# ERP Payroll Management System

A production-grade Spring Boot REST API for Government ERP Payroll Management System with JWT authentication, role-based access control, and comprehensive payroll processing capabilities.

## 📋 Table of Contents
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Database Setup](#database-setup)
- [Installation & Running](#installation--running)
- [API Documentation](#api-documentation)
- [Default Credentials](#default-credentials)
- [API Endpoints](#api-endpoints)
- [Testing with Swagger](#testing-with-swagger)
- [Project Structure](#project-structure)

## ✨ Features

- ✅ JWT-based Authentication & Authorization
- ✅ Role-based Access Control (ADMIN, MANAGER, EMPLOYEE)
- ✅ Employee Management (CRUD operations)
- ✅ Employment Management with salary tracking
- ✅ Deduction Management with configurable rates
- ✅ Automated Payroll Generation
- ✅ PaySlip Management with approval workflow
- ✅ Email Notifications on salary approval
- ✅ Duplicate payroll prevention (unique constraint)
- ✅ Comprehensive validation & error handling
- ✅ Swagger/OpenAPI documentation
- ✅ Audit trails with timestamps
- ✅ BCrypt password encoding

## 🛠 Tech Stack

- **Framework**: Spring Boot 3.5.14
- **Security**: Spring Security 6 with JWT
- **Database**: MySQL 8
- **ORM**: Spring Data JPA + Hibernate
- **Java Version**: 17
- **Build Tool**: Maven
- **Documentation**: SpringDoc OpenAPI 3 (Swagger UI)
- **Additional Libraries**:
  - Lombok
  - ModelMapper
  - JJWT (JWT handling)
  - JavaMailSender
  - Thymeleaf

## 📦 Prerequisites

Before running this application, ensure you have:

- **Java 17** or higher installed
- **Maven 3.8+** installed
- **MySQL 8** installed and running
- **Git** (optional, for cloning)
- An IDE like IntelliJ IDEA or Eclipse (optional)

## 🗄 Database Setup

### Step 1: Create MySQL Database

Open MySQL command line or MySQL Workbench and run:

```sql
CREATE DATABASE erp_payroll_db;
```

### Step 2: Configure Database Credentials

Open `src/main/resources/application.properties` and update:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/erp_payroll_db
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD
```

Replace `YOUR_MYSQL_PASSWORD` with your actual MySQL root password (or leave empty if no password).

## 🚀 Installation & Running

### Option 1: Using Maven Command Line

```bash
# Clone the repository (if using Git)
git clone <repository-url>
cd payroll-management

# Clean and build the project
mvnw clean install

# Run the application
mvnw spring-boot:run
```

### Option 2: Using IDE

1. Open the project in your IDE (IntelliJ IDEA, Eclipse, etc.)
2. Wait for Maven to download dependencies
3. Run `PayrollManagementApplication.java`

The application will start on **http://localhost:8080**

## 📚 API Documentation

Once the application is running, access Swagger UI at:

**http://localhost:8080/swagger-ui.html**

OpenAPI JSON specification:

**http://localhost:8080/v3/api-docs**

## 🔑 Default Credentials

The system comes pre-loaded with 3 test users:

| Role | Email | Password |
|------|-------|----------|
| ADMIN | admin@erp.rw | Admin@123 |
| MANAGER | manager@erp.rw | Manager@123 |
| EMPLOYEE | employee@erp.rw | Employee@123 |

## 📡 API Endpoints

### Authentication (Public)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/auth/login` | Login and get JWT token |

### Employees (MANAGER, ADMIN)

| Method | Endpoint | Description | Roles |
|--------|----------|-------------|-------|
| POST | `/api/v1/employees` | Create employee | MANAGER, ADMIN |
| GET | `/api/v1/employees` | Get all employees | MANAGER, ADMIN |
| GET | `/api/v1/employees/{id}` | Get employee by ID | MANAGER, ADMIN |
| PUT | `/api/v1/employees/{id}` | Update employee | MANAGER, ADMIN |
| DELETE | `/api/v1/employees/{id}` | Delete employee | MANAGER, ADMIN |
| PATCH | `/api/v1/employees/{id}/activate` | Activate employee | MANAGER, ADMIN |
| PATCH | `/api/v1/employees/{id}/deactivate` | Deactivate employee | MANAGER, ADMIN |

### Employment (MANAGER)

| Method | Endpoint | Description | Roles |
|--------|----------|-------------|-------|
| POST | `/api/v1/employment` | Create employment | MANAGER |
| GET | `/api/v1/employment` | Get all employments | MANAGER |
| GET | `/api/v1/employment/{id}` | Get employment by ID | MANAGER |
| PUT | `/api/v1/employment/{id}` | Update employment | MANAGER |
| DELETE | `/api/v1/employment/{id}` | Delete employment | MANAGER |

### Deductions (MANAGER)

| Method | Endpoint | Description | Roles |
|--------|----------|-------------|-------|
| POST | `/api/v1/deductions` | Create deduction | MANAGER |
| GET | `/api/v1/deductions` | Get all deductions | MANAGER |
| GET | `/api/v1/deductions/{id}` | Get deduction by ID | MANAGER |
| PUT | `/api/v1/deductions/{id}` | Update deduction | MANAGER |
| DELETE | `/api/v1/deductions/{id}` | Delete deduction | MANAGER |

### PaySlips (Mixed Roles)

| Method | Endpoint | Description | Roles |
|--------|----------|-------------|-------|
| POST | `/api/v1/payslips/generate/{month}/{year}` | Generate payroll | MANAGER |
| GET | `/api/v1/payslips` | Get payslips (filtered by role) | ALL |
| GET | `/api/v1/payslips/{employeeId}/{month}/{year}` | Get specific payslip | MANAGER, ADMIN |
| PATCH | `/api/v1/payslips/{id}/approve` | Approve payslip (sends email) | ADMIN |

## 🧪 Testing with Swagger

### Step 1: Login

1. Go to **http://localhost:8080/swagger-ui.html**
2. Find the **Authentication** section
3. Click on `POST /api/v1/auth/login`
4. Click "Try it out"
5. Enter credentials:
```json
{
  "email": "admin@erp.rw",
  "password": "Admin@123"
}
```
6. Click "Execute"
7. Copy the `token` from the response

### Step 2: Authorize

1. Click the **Authorize** button (🔒) at the top right
2. Enter: `Bearer YOUR_TOKEN_HERE` (replace with the token you copied)
3. Click "Authorize"
4. Now you can test all protected endpoints!

### Step 3: Test Workflow

#### 3.1 Create an Employee
```json
POST /api/v1/employees
{
  "firstName": "Jane",
  "lastName": "Smith",
  "email": "jane.smith@erp.rw",
  "password": "Password@123",
  "mobile": "0781234570",
  "dateOfBirth": "1992-03-15",
  "role": "ROLE_EMPLOYEE",
  "status": "ACTIVE"
}
```

#### 3.2 Create Employment for the Employee
```json
POST /api/v1/employment
{
  "employeeId": 4,
  "department": "IT",
  "position": "Software Developer",
  "baseSalary": 70000,
  "status": "ACTIVE",
  "joiningDate": "2025-01-01"
}
```

#### 3.3 Generate Payroll (Login as MANAGER)
```
POST /api/v1/payslips/generate/6/2025
```
This generates payslips for June 2025 for all active employees.

#### 3.4 Approve PaySlip (Login as ADMIN)
```
PATCH /api/v1/payslips/1/approve
```
This approves the payslip and sends an email notification.

## 📁 Project Structure

```
rw.gov.erp.payroll/
├── config/              # Configuration classes (Security, OpenAPI, etc.)
├── controller/          # REST Controllers
├── dto/                 # Data Transfer Objects
│   ├── request/        # Request DTOs
│   └── response/       # Response DTOs
├── entity/             # JPA Entities
├── enums/              # Enumerations
├── exception/          # Custom Exceptions & Global Handler
├── repository/         # JPA Repositories
├── security/           # JWT & Security Components
├── service/            # Service Interfaces
├── serviceImpl/        # Service Implementations
└── utils/              # Utility Classes
```

## 💰 Salary Calculation Logic

Given a base salary, the system calculates:

```
houseAmount = baseSalary × 14%
transportAmount = baseSalary × 14%
grossSalary = baseSalary + houseAmount + transportAmount

employeeTax = baseSalary × 30%
pension = baseSalary × 6%
medicalInsurance = baseSalary × 5%
others = baseSalary × 5%

netSalary = grossSalary - (employeeTax + pension + medicalInsurance + others)
```

### Example (Base Salary = 70,000 RWF):
- House: 9,800
- Transport: 9,800
- **Gross: 89,600**
- Tax: 21,000
- Pension: 4,200
- Medical: 3,500
- Others: 3,500
- **Net Salary: 57,400 RWF**

## 📧 Email Configuration

To enable email notifications, update `application.properties`:

```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your_email@gmail.com
spring.mail.password=your_app_password
```

For Gmail, you need to create an **App Password**:
1. Enable 2-Factor Authentication
2. Go to Google Account Settings → Security → App Passwords
3. Generate a new app password
4. Use that password in the configuration

## 🔒 Security Features

- **Stateless JWT Authentication**: No sessions, pure JWT tokens
- **BCrypt Password Encoding**: Passwords are securely hashed
- **Role-Based Access Control**: Method-level security with @PreAuthorize
- **CORS Configuration**: Configured for development
- **Exception Handling**: Comprehensive error responses

## 🐛 Troubleshooting

### Database Connection Error
```
Ensure MySQL is running and credentials in application.properties are correct
```

### Port 8080 Already in Use
```
Change the port in application.properties:
server.port=8081
```

### JWT Token Expired
```
Login again to get a new token (tokens expire after 24 hours)
```

## 📝 Notes

- The system prevents duplicate payroll generation for the same employee in the same month/year
- Deductions are pre-loaded on first startup with updated rates (Pension = 6%)
- All timestamps are automatically managed by JPA Auditing
- Employee codes are auto-generated (EMP001, EMP002, ...)
- Employment codes are auto-generated (EMPLOY001, EMPLOY002, ...)
- Deduction codes are auto-generated (DED001, DED002, ...)

## 👨‍💻 Development

Built for the Rwanda Government ERP System as part of the National Java Practical Exam.

**Version**: 1.0  
**Spring Boot Version**: 3.5.14  
**Java Version**: 17  

---

For any issues or questions, refer to the Swagger documentation or check the application logs.
