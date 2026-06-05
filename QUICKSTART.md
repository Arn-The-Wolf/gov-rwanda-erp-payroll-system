# 🚀 Quick Start Guide - ERP Payroll Management System

## ⚡ 5-Minute Setup

### Step 1: Create PostgreSQL Database (1 minute)

Open PostgreSQL pgAdmin or psql command line and run:

```sql
CREATE DATABASE erp_payroll_db;
```

Or use the provided script:
```bash
psql -U postgres -f database-setup.sql
```

### Step 2: Configure Database Password (30 seconds)

Open `src/main/resources/application.properties` and set your PostgreSQL password:

```properties
spring.datasource.password=YOUR_POSTGRES_PASSWORD_HERE
```

### Step 3: Run the Application (30 seconds)

```bash
mvnw spring-boot:run
```

Wait for the message: **"Started PayrollManagementApplication in X seconds"**

### Step 4: Access Swagger UI (1 minute)

Open your browser: **http://localhost:8080/swagger-ui.html**

### Step 5: Login and Test (2 minutes)

1. Click on **POST /api/v1/auth/login**
2. Click **"Try it out"**
3. Use these credentials:

```json
{
  "email": "manager@erp.rw",
  "password": "Manager@123"
}
```

4. Click **"Execute"**
5. Copy the `token` from the response
6. Click the **🔒 Authorize** button at the top
7. Enter: `Bearer YOUR_TOKEN_HERE`
8. Click **"Authorize"**

**You're ready to test all endpoints!**

---

## 📋 Default Test Accounts

| Role | Email | Password | Can Do |
|------|-------|----------|---------|
| **ADMIN** | admin@erp.rw | Admin@123 | Approve payslips, manage employees |
| **MANAGER** | manager@erp.rw | Manager@123 | Create employees, generate payroll, manage deductions |
| **EMPLOYEE** | employee@erp.rw | Employee@123 | View own payslips |

---

## 🎯 Common Workflows

### Workflow 1: Generate Payroll (MANAGER)

1. **Login as MANAGER** (see Step 5 above)
2. **Create an Employment Record**
   - Go to `POST /api/v1/employment`
   - Employee ID: `3` (John Doe - pre-loaded)
   - Base Salary: `70000`
   - Department: `IT`
   - Position: `Developer`
   - Joining Date: `2025-01-01`

3. **Generate Payroll**
   - Go to `POST /api/v1/payslips/generate/{month}/{year}`
   - Month: `6`
   - Year: `2025`
   - This generates payslips for ALL active employees

4. **View Generated Payslips**
   - Go to `GET /api/v1/payslips`

### Workflow 2: Approve Payslip (ADMIN)

1. **Login as ADMIN** (email: admin@erp.rw, password: Admin@123)
2. **Get Payslip ID** from Workflow 1
3. **Approve Payslip**
   - Go to `PATCH /api/v1/payslips/{id}/approve`
   - Enter the payslip ID
   - This changes status to PAID and sends email

### Workflow 3: View Own Payslip (EMPLOYEE)

1. **Login as EMPLOYEE** (email: employee@erp.rw, password: Employee@123)
2. **View Payslips**
   - Go to `GET /api/v1/payslips`
   - Employee sees only their own payslips

---

## 🔧 Pre-Loaded Data

The system comes with:

✅ **6 Deductions** (with updated 2025 rates):
- EmployeeTax: 30%
- Pension: **6%** (updated from 3%)
- MedicalInsurance: 5%
- Others: 5%
- Housing: 14% (added to gross)
- Transport: 14% (added to gross)

✅ **3 Test Employees**:
- Admin User (ROLE_ADMIN)
- Manager User (ROLE_MANAGER)
- John Doe (ROLE_EMPLOYEE)

---

## 📊 Sample Payslip Calculation

For **Base Salary = 70,000 RWF**:

```
House Amount:          9,800  (70,000 × 14%)
Transport Amount:      9,800  (70,000 × 14%)
─────────────────────────────
Gross Salary:         89,600  (70,000 + 9,800 + 9,800)

Employee Tax:         21,000  (70,000 × 30%)
Pension:               4,200  (70,000 × 6%)
Medical Insurance:     3,500  (70,000 × 5%)
Others:                3,500  (70,000 × 5%)
─────────────────────────────
Total Deductions:     32,200

NET SALARY:           57,400  (89,600 - 32,200)
```

---

## 🐛 Troubleshooting

### Error: "Access Denied for user 'root'@'localhost'"
**Solution**: Check your MySQL password in `application.properties`

### Error: "Port 8080 already in use"
**Solution**: Change port in `application.properties`:
```properties
server.port=8081
```

### Error: "Cannot generate payroll - No active employment found"
**Solution**: Create an employment record first using `POST /api/v1/employment`

### Error: "JWT token expired"
**Solution**: Login again to get a new token (tokens expire after 24 hours)

### Email not sending
**Solution**: Configure Gmail SMTP settings in `application.properties`:
```properties
spring.mail.username=your_email@gmail.com
spring.mail.password=your_app_password
```
(Use Gmail App Password, not regular password)

---

## 📡 API Endpoint Summary

### Public Endpoints
- `POST /api/v1/auth/login` - Login

### Employee Management (MANAGER, ADMIN)
- `POST /api/v1/employees` - Create
- `GET /api/v1/employees` - List all
- `PUT /api/v1/employees/{id}` - Update
- `PATCH /api/v1/employees/{id}/activate` - Activate
- `PATCH /api/v1/employees/{id}/deactivate` - Deactivate

### Employment Management (MANAGER)
- `POST /api/v1/employment` - Create
- `GET /api/v1/employment` - List all
- `PUT /api/v1/employment/{id}` - Update

### Deduction Management (MANAGER)
- `POST /api/v1/deductions` - Create
- `GET /api/v1/deductions` - List all
- `PUT /api/v1/deductions/{id}` - Update

### Payroll Management
- `POST /api/v1/payslips/generate/{month}/{year}` - Generate (MANAGER)
- `GET /api/v1/payslips` - View (ALL - filtered by role)
- `PATCH /api/v1/payslips/{id}/approve` - Approve (ADMIN)

---

## 🎓 Learning Resources

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Documentation**: http://localhost:8080/v3/api-docs
- **Full README**: See `README.md` for detailed documentation

---

## ✅ Success Checklist

- [ ] MySQL database created
- [ ] Application.properties configured
- [ ] Application started successfully
- [ ] Swagger UI accessible
- [ ] Successfully logged in and got JWT token
- [ ] Token added to Swagger authorization
- [ ] Tested at least one endpoint
- [ ] Created employment record
- [ ] Generated payroll
- [ ] Approved payslip

**All checked? Congratulations! 🎉 You're ready for the exam!**

---

## 💡 Pro Tips

1. **Keep Swagger UI open** - It's your best friend for testing
2. **Use MANAGER account** for most operations
3. **Always authorize** after login before testing other endpoints
4. **Check Response Codes**: 200/201 = Success, 401 = Need to login, 403 = Wrong role
5. **Employment must exist** before generating payroll
6. **Can't generate twice** for same employee in same month/year

---

## 📞 Need Help?

- Check application logs in the console
- Verify MySQL is running: `mysql -u root -p`
- Ensure port 8080 is free
- Review error messages in Swagger responses

**Good luck with your exam! 🚀**
