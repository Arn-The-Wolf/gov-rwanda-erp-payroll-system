# 🏗 System Architecture - ERP Payroll Management

## 📐 System Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                         CLIENT LAYER                             │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐          │
│  │   Browser    │  │   Postman    │  │   Swagger    │          │
│  │              │  │              │  │      UI      │          │
│  └──────────────┘  └──────────────┘  └──────────────┘          │
└──────────────────────────┬──────────────────────────────────────┘
                           │ HTTPS / REST API
                           │ (JSON Request/Response)
┌──────────────────────────▼──────────────────────────────────────┐
│                    PRESENTATION LAYER                            │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │              REST CONTROLLERS                               │ │
│  │  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐     │ │
│  │  │   Auth   │ │ Employee │ │Employment│ │Deduction │     │ │
│  │  │Controller│ │Controller│ │Controller│ │Controller│ ...  │ │
│  │  └──────────┘ └──────────┘ └──────────┘ └──────────┘     │ │
│  │          @RestController + @RequestMapping                 │ │
│  └────────────────────────────────────────────────────────────┘ │
└──────────────────────────┬──────────────────────────────────────┘
                           │
┌──────────────────────────▼──────────────────────────────────────┐
│                     SECURITY LAYER                               │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │              SPRING SECURITY FILTERS                        │ │
│  │  ┌──────────────────────┐  ┌──────────────────────┐       │ │
│  │  │ JwtAuthentication    │  │  Authorization       │       │ │
│  │  │     Filter           │─▶│   Filter             │       │ │
│  │  └──────────────────────┘  └──────────────────────┘       │ │
│  │  ┌──────────────────────┐  ┌──────────────────────┐       │ │
│  │  │   JwtTokenProvider   │  │CustomUserDetails     │       │ │
│  │  │  (Generate/Validate) │  │    Service           │       │ │
│  │  └──────────────────────┘  └──────────────────────┘       │ │
│  │           BCrypt Password Encoder                          │ │
│  └────────────────────────────────────────────────────────────┘ │
└──────────────────────────┬──────────────────────────────────────┘
                           │
┌──────────────────────────▼──────────────────────────────────────┐
│                    BUSINESS LOGIC LAYER                          │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │                SERVICE INTERFACES                           │ │
│  │  AuthService │ EmployeeService │ EmploymentService │ ...   │ │
│  └────────────────────────────────────────────────────────────┘ │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │             SERVICE IMPLEMENTATIONS                         │ │
│  │  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐     │ │
│  │  │  Auth    │ │ Employee │ │Employment│ │ PaySlip  │     │ │
│  │  │ServiceImp│ │ServiceImp│ │ServiceImp│ │ServiceImp│ ... │ │
│  │  └──────────┘ └──────────┘ └──────────┘ └──────────┘     │ │
│  │        @Service + @Transactional                           │ │
│  └────────────────────────────────────────────────────────────┘ │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │                    UTILITIES                                │ │
│  │  ┌──────────────────┐  ┌──────────────────┐               │ │
│  │  │PayrollCalculator │  │  ModelMapper     │               │ │
│  │  │  (Salary Logic)  │  │  (DTO Mapping)   │               │ │
│  │  └──────────────────┘  └──────────────────┘               │ │
│  └────────────────────────────────────────────────────────────┘ │
└──────────────────────────┬──────────────────────────────────────┘
                           │
┌──────────────────────────▼──────────────────────────────────────┐
│                   DATA ACCESS LAYER                              │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │          SPRING DATA JPA REPOSITORIES                       │ │
│  │  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐     │ │
│  │  │Employee  │ │Employment│ │Deduction │ │ PaySlip  │     │ │
│  │  │Repository│ │Repository│ │Repository│ │Repository│     │ │
│  │  └──────────┘ └──────────┘ └──────────┘ └──────────┘     │ │
│  │       extends JpaRepository<Entity, ID>                    │ │
│  └────────────────────────────────────────────────────────────┘ │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │                  JPA ENTITIES                               │ │
│  │  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐     │ │
│  │  │ Employee │ │Employment│ │ Deduction│ │ PaySlip  │     │ │
│  │  │  Entity  │ │  Entity  │ │  Entity  │ │  Entity  │     │ │
│  │  └──────────┘ └──────────┘ └──────────┘ └──────────┘     │ │
│  │         @Entity + @Table + JPA Annotations                 │ │
│  └────────────────────────────────────────────────────────────┘ │
└──────────────────────────┬──────────────────────────────────────┘
                           │ JDBC
                           │ (Hibernate ORM)
┌──────────────────────────▼──────────────────────────────────────┐
│                     DATABASE LAYER                               │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │                      MySQL 8                                │ │
│  │  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐     │ │
│  │  │employees │ │employments│ │deductions│ │ payslips │     │ │
│  │  │  table   │ │   table  │ │  table   │ │  table   │     │ │
│  │  └──────────┘ └──────────┘ └──────────┘ └──────────┘     │ │
│  └────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                    EXTERNAL SERVICES                             │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │              EMAIL SERVICE                                  │ │
│  │  ┌──────────────────┐  ┌──────────────────┐               │ │
│  │  │ JavaMailSender   │─▶│  Gmail SMTP      │               │ │
│  │  │  (Spring Boot)   │  │  Server          │               │ │
│  │  └──────────────────┘  └──────────────────┘               │ │
│  └────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
```

---

## 🔄 Request Flow Diagram

### Authentication Flow
```
┌─────────┐     POST /api/v1/auth/login        ┌─────────────┐
│ Client  │───────────────────────────────────▶│AuthController│
└─────────┘        { email, password }         └──────┬──────┘
     ▲                                                 │
     │                                                 ▼
     │                                        ┌────────────────┐
     │                                        │  AuthService   │
     │                                        └────────┬───────┘
     │                                                 │
     │                                                 ▼
     │                                        ┌────────────────┐
     │                                        │Authentication  │
     │                                        │   Manager      │
     │                                        └────────┬───────┘
     │                                                 │
     │                                                 ▼
     │                                        ┌────────────────┐
     │                                        │CustomUserDetails│
     │                                        │    Service     │
     │                                        └────────┬───────┘
     │                                                 │
     │                                                 ▼
     │                                        ┌────────────────┐
     │                                        │Employee        │
     │                                        │Repository      │
     │                                        └────────┬───────┘
     │                                                 │
     │                                                 ▼
     │                                        ┌────────────────┐
     │                                        │JwtTokenProvider│
     │              JWT Token                 │(Generate Token)│
     └────────────────────────────────────────┴────────────────┘
```

### Secured Endpoint Flow
```
┌─────────┐   POST /api/v1/payslips/generate   ┌─────────────┐
│ Client  │───────────────────────────────────▶│PaySlipController│
└─────────┘  Authorization: Bearer <token>     └──────┬──────┘
     ▲                                                 │
     │                                                 │
     │                                                 ▼
     │                                        ┌────────────────┐
     │                                        │JwtAuthentication│
     │                                        │     Filter     │
     │                                        └────────┬───────┘
     │                                                 │
     │                                                 ▼
     │                                        ┌────────────────┐
     │                                        │JwtTokenProvider│
     │                                        │(Validate Token)│
     │                                        └────────┬───────┘
     │                                                 │
     │                                                 ▼
     │                                        ┌────────────────┐
     │                                        │@PreAuthorize   │
     │                                        │(Check Role)    │
     │                                        └────────┬───────┘
     │                                                 │
     │                                                 ▼
     │                                        ┌────────────────┐
     │                                        │PaySlipService  │
     │                                        └────────┬───────┘
     │                                                 │
     │                                                 ▼
     │                                        ┌────────────────┐
     │                                        │PayrollCalculator│
     │                                        │(Calculate)     │
     │                                        └────────┬───────┘
     │                                                 │
     │                                                 ▼
     │                                        ┌────────────────┐
     │                                        │PaySlipRepository│
     │              PaySlip Response          │(Save to DB)    │
     └────────────────────────────────────────┴────────────────┘
```

### Payslip Approval Flow
```
┌─────────┐  PATCH /api/v1/payslips/{id}/approve  ┌─────────────┐
│  ADMIN  │──────────────────────────────────────▶│PaySlipController│
└─────────┘   Authorization: Bearer <admin_token> └──────┬──────┘
     ▲                                                    │
     │                                                    ▼
     │                                           ┌────────────────┐
     │                                           │PaySlipService  │
     │                                           │(Approve)       │
     │                                           └────────┬───────┘
     │                                                    │
     │                                                    ├──────┐
     │                                                    │      │
     │                                                    ▼      ▼
     │                                           ┌────────────────┐
     │                                           │Update Status:  │
     │                                           │PENDING → PAID  │
     │                                           └────────┬───────┘
     │                                                    │
     │                                                    ▼
     │                                           ┌────────────────┐
     │                                           │EmailService    │
     │                                           │(Send Email)    │
     │                                           └────────┬───────┘
     │                                                    │
     │                                                    ▼
     │                                           ┌────────────────┐
     │                                           │JavaMailSender  │
     │            Success Response               │(SMTP Gmail)    │
     └───────────────────────────────────────────┴────────────────┘
                                                         │
                                                         ▼
                                                ┌────────────────┐
                                                │Employee Email  │
                                                │(Notification)  │
                                                └────────────────┘
```

---

## 📦 Package Structure Breakdown

```
rw.gov.erp.payroll
├── config/                         [Configuration Classes]
│   ├── ApplicationConfig.java      • Enables JPA Auditing
│   ├── DataLoader.java             • Seeds default data on startup
│   ├── ModelMapperConfig.java      • Bean for DTO mapping
│   ├── OpenApiConfig.java          • Swagger UI configuration
│   └── SecurityConfig.java         • Spring Security + JWT setup
│
├── controller/                     [REST API Layer]
│   ├── AuthController.java         • Login endpoint
│   ├── EmployeeController.java     • Employee CRUD + activate/deactivate
│   ├── EmploymentController.java   • Employment CRUD
│   ├── DeductionController.java    • Deduction CRUD
│   └── PaySlipController.java      • Payroll generation + approval
│
├── dto/                            [Data Transfer Objects]
│   ├── request/                    • Request payload models
│   │   ├── LoginRequest.java       • Login credentials
│   │   ├── EmployeeRequest.java    • Employee creation/update
│   │   ├── EmploymentRequest.java  • Employment creation/update
│   │   ├── DeductionRequest.java   • Deduction creation/update
│   │   └── PaySlipRequest.java     • PaySlip generation params
│   └── response/                   • Response models
│       ├── ApiResponse.java        • Generic API response wrapper
│       ├── AuthResponse.java       • JWT token response
│       ├── EmployeeResponse.java   • Employee data (no password)
│       ├── EmploymentResponse.java • Employment data
│       ├── DeductionResponse.java  • Deduction data
│       └── PaySlipResponse.java    • PaySlip data with calculations
│
├── entity/                         [JPA Entities]
│   ├── Employee.java               • User/Employee entity
│   ├── Employment.java             • Employment record entity
│   ├── Deduction.java              • Deduction rule entity
│   └── PaySlip.java                • PaySlip entity (unique on employee+month+year)
│
├── enums/                          [Enumerations]
│   ├── Role.java                   • ROLE_ADMIN, ROLE_MANAGER, ROLE_EMPLOYEE
│   ├── EmployeeStatus.java         • ACTIVE, DISABLED
│   ├── EmploymentStatus.java       • ACTIVE, INACTIVE
│   ├── DeductionStatus.java        • ACTIVE, INACTIVE
│   └── PaySlipStatus.java          • PENDING, PAID
│
├── exception/                      [Exception Handling]
│   ├── BadRequestException.java    • 400 errors (validation, business rules)
│   ├── ResourceNotFoundException   • 404 errors (entity not found)
│   ├── DuplicateResourceException  • 409 errors (unique constraint violations)
│   └── GlobalExceptionHandler      • @RestControllerAdvice for all exceptions
│
├── repository/                     [Data Access Layer]
│   ├── EmployeeRepository.java     • JPA repo for employees
│   ├── EmploymentRepository.java   • JPA repo for employments
│   ├── DeductionRepository.java    • JPA repo for deductions
│   └── PaySlipRepository.java      • JPA repo for payslips
│
├── security/                       [Security Components]
│   ├── JwtTokenProvider.java       • Generate & validate JWT tokens
│   ├── JwtAuthenticationFilter     • Intercept requests, extract JWT
│   ├── JwtAuthenticationEntryPoint • Handle 401 errors
│   └── CustomUserDetailsService    • Load user by email for authentication
│
├── service/                        [Service Interfaces]
│   ├── AuthService.java            • Authentication operations
│   ├── EmployeeService.java        • Employee business logic interface
│   ├── EmploymentService.java      • Employment business logic interface
│   ├── DeductionService.java       • Deduction business logic interface
│   ├── PaySlipService.java         • Payslip business logic interface
│   └── EmailService.java           • Email sending interface
│
├── serviceImpl/                    [Service Implementations]
│   ├── AuthServiceImpl.java        • Login logic + JWT generation
│   ├── EmployeeServiceImpl.java    • Employee CRUD + password encoding
│   ├── EmploymentServiceImpl.java  • Employment CRUD
│   ├── DeductionServiceImpl.java   • Deduction CRUD
│   ├── PaySlipServiceImpl.java     • Payroll generation + approval + email
│   └── EmailServiceImpl.java       • Send email via JavaMailSender
│
├── utils/                          [Utility Classes]
│   └── PayrollCalculator.java      • Salary calculation logic
│
└── PayrollManagementApplication    [Main Application Class]
    └── @SpringBootApplication      • Entry point
```

---

## 🗄 Database Schema

```
┌────────────────────────────────┐
│         employees              │
├────────────────────────────────┤
│ id (PK)                        │
│ code (UNIQUE)                  │
│ first_name                     │
│ last_name                      │
│ email (UNIQUE)                 │
│ password (BCrypt)              │
│ mobile                         │
│ date_of_birth                  │
│ role (ENUM)                    │
│ status (ENUM)                  │
│ created_at                     │
│ updated_at                     │
└────────────────────────────────┘
         │
         │ 1
         │
         │ N
         ▼
┌────────────────────────────────┐
│        employments             │
├────────────────────────────────┤
│ id (PK)                        │
│ code (UNIQUE)                  │
│ employee_id (FK)               │───┐
│ department                     │   │
│ position                       │   │
│ base_salary                    │   │
│ status (ENUM)                  │   │
│ joining_date                   │   │
│ created_at                     │   │
│ updated_at                     │   │
└────────────────────────────────┘   │
                                     │
         ┌───────────────────────────┘
         │
         │ N                     1
         │                       │
         ▼                       ▼
┌────────────────────────────────┐
│          payslips              │
├────────────────────────────────┤
│ id (PK)                        │
│ employee_id (FK)               │
│ house_amount                   │
│ transport_amount               │
│ employee_taxed_amount          │
│ pension_amount                 │
│ medical_insurance_amount       │
│ other_taxed_amount             │
│ gross_salary                   │
│ net_salary                     │
│ month                          │
│ year                           │
│ status (ENUM)                  │
│ created_at                     │
│ updated_at                     │
├────────────────────────────────┤
│ UNIQUE(employee_id, month, year)│
└────────────────────────────────┘

┌────────────────────────────────┐
│         deductions             │
├────────────────────────────────┤
│ id (PK)                        │
│ code (UNIQUE)                  │
│ deduction_name (UNIQUE)        │
│ percentage                     │
│ status (ENUM)                  │
│ created_at                     │
│ updated_at                     │
└────────────────────────────────┘
```

---

## 🔐 Security Architecture

```
┌─────────────────────────────────────────────────────────┐
│                    CLIENT REQUEST                        │
│    POST /api/v1/payslips/generate/6/2025               │
│    Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...        │
└────────────────────┬────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────┐
│          JwtAuthenticationFilter                        │
│  1. Extract token from Authorization header             │
│  2. Validate token (signature, expiration)              │
│  3. Extract email from token                            │
│  4. Load UserDetails from database                      │
│  5. Create Authentication object                        │
│  6. Set in SecurityContext                              │
└────────────────────┬────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────┐
│            Authorization Filter                          │
│  1. Check if endpoint requires authentication           │
│  2. Verify user has required role                       │
│  3. Execute @PreAuthorize("hasRole('MANAGER')")         │
└────────────────────┬────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────┐
│              Controller Method Execution                 │
│  PaySlipController.generatePayroll(month, year)         │
└─────────────────────────────────────────────────────────┘
```

### JWT Token Structure
```json
{
  "header": {
    "alg": "HS256",
    "typ": "JWT"
  },
  "payload": {
    "sub": "manager@erp.rw",
    "role": "ROLE_MANAGER",
    "iat": 1717344000,
    "exp": 1717430400
  },
  "signature": "HMACSHA256(base64UrlEncode(header) + '.' + base64UrlEncode(payload), secret)"
}
```

---

## 🎯 Key Design Patterns Used

1. **MVC Pattern** - Controllers, Services, Repositories
2. **DTO Pattern** - Separate request/response models from entities
3. **Repository Pattern** - Spring Data JPA repositories
4. **Service Layer Pattern** - Interface + Implementation
5. **Builder Pattern** - Lombok @Builder on entities
6. **Dependency Injection** - Spring's @Autowired / Constructor injection
7. **Strategy Pattern** - Different roles have different access strategies
8. **Factory Pattern** - JWT token creation
9. **Filter Pattern** - Security filter chain
10. **Template Method Pattern** - JPA repository methods

---

## 📊 System Components Summary

| Component | Count | Technology |
|-----------|-------|------------|
| Controllers | 5 | Spring MVC |
| Services | 6 | Spring @Service |
| Repositories | 4 | Spring Data JPA |
| Entities | 4 | JPA + Hibernate |
| DTOs | 12 | POJOs |
| Enums | 5 | Java Enums |
| Security Filters | 2 | Spring Security |
| Utility Classes | 1 | Pure Java |
| Config Classes | 5 | Spring @Configuration |
| Exception Handlers | 4 | @RestControllerAdvice |

**Total: 56+ Java Classes**

---

*This architecture follows clean code principles, SOLID design patterns, and Spring Boot best practices.*
