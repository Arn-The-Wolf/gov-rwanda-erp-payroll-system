# 🐘 PostgreSQL Setup Guide

## ✅ Migration Complete!

The ERP Payroll Management System has been successfully migrated from MySQL to PostgreSQL!

---

## 📋 What Changed

### 1. Dependencies (pom.xml)
**Before (MySQL):**
```xml
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>
```

**After (PostgreSQL):**
```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

### 2. Database Configuration (application.properties)
**Before (MySQL):**
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/erp_payroll_db
spring.datasource.username=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
```

**After (PostgreSQL):**
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/erp_payroll_db
spring.datasource.username=postgres
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.properties.hibernate.jdbc.lob.non_contextual_creation=true
```

### 3. Database Setup Script
- `CREATE DATABASE IF NOT EXISTS` → `CREATE DATABASE` (PostgreSQL syntax)
- `USE erp_payroll_db` → `\c erp_payroll_db` (psql command)
- `SHOW TABLES` → `SELECT table_name FROM information_schema.tables`
- `DESCRIBE table` → `\d table` (psql command)

### 4. Database Triggers & Procedures
**Major Changes:**
- MySQL `DELIMITER $$` → Not needed in PostgreSQL
- MySQL `PROCEDURE` → PostgreSQL `FUNCTION`
- MySQL `BEGIN...END` → PostgreSQL `BEGIN...END; $$ LANGUAGE plpgsql;`
- MySQL `SET variable =` → PostgreSQL `variable :=`
- MySQL `FORMAT(number, 2)` → PostgreSQL `TO_CHAR(number, 'FM999999999.00')`
- MySQL `NOW()` → PostgreSQL `CURRENT_TIMESTAMP`
- MySQL `CONCAT()` → PostgreSQL `||` operator
- MySQL `LPAD(v_month, 2, '0')` → PostgreSQL `LPAD(v_month::TEXT, 2, '0')`
- MySQL triggers use procedures → PostgreSQL triggers use trigger functions

---

## 🚀 Quick Setup (5 Minutes)

### Step 1: Install PostgreSQL

**If you don't have PostgreSQL installed:**

**Windows:**
- Download from: https://www.postgresql.org/download/windows/
- Run installer
- Remember the password you set for the `postgres` user

**Linux (Ubuntu/Debian):**
```bash
sudo apt update
sudo apt install postgresql postgresql-contrib
```

**Mac:**
```bash
brew install postgresql
brew services start postgresql
```

### Step 2: Create Database (1 minute)

**Option A: Using psql command line**
```bash
# Connect to PostgreSQL
psql -U postgres

# Create database
CREATE DATABASE erp_payroll_db;

# Exit psql
\q
```

**Option B: Using pgAdmin**
1. Open pgAdmin
2. Right-click on "Databases"
3. Create → Database
4. Name: `erp_payroll_db`
5. Click "Save"

**Option C: Using provided script**
```bash
psql -U postgres -f database-setup.sql
```

### Step 3: Configure Password (30 seconds)

Edit `src/main/resources/application.properties`:

```properties
# Change this line:
spring.datasource.password=YOUR_POSTGRES_PASSWORD
```

**Default PostgreSQL port:** 5432  
**Default username:** postgres

### Step 4: Run Application (2 minutes)

```bash
.\mvnw spring-boot:run
```

Wait for: `Started PayrollManagementApplication`

### Step 5: Install Database Triggers (30 seconds)

**Option A: Using psql**
```bash
psql -U postgres -d erp_payroll_db -f database-triggers.sql
```

**Option B: Using pgAdmin**
1. Open pgAdmin
2. Connect to `erp_payroll_db`
3. Tools → Query Tool
4. Open `database-triggers.sql`
5. Click Execute (F5)

### Step 6: Verify (1 minute)

**Check tables created:**
```sql
-- In psql or pgAdmin Query Tool
\c erp_payroll_db
\dt
```

Expected output:
```
             List of relations
 Schema |     Name     | Type  |  Owner   
--------+--------------+-------+----------
 public | deductions   | table | postgres
 public | employees    | table | postgres
 public | employments  | table | postgres
 public | messages     | table | postgres
 public | payslips     | table | postgres
 public | users        | table | postgres
```

**Check functions/triggers:**
```sql
-- Check functions
\df

-- Check triggers
SELECT trigger_name, event_object_table 
FROM information_schema.triggers;
```

---

## 🔧 PostgreSQL-Specific Configuration

### Connection Pool Settings (Optional)

Add to `application.properties` for better performance:

```properties
# HikariCP settings for PostgreSQL
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.idle-timeout=600000
spring.datasource.hikari.max-lifetime=1800000
```

### PostgreSQL Performance Settings

```properties
# PostgreSQL-specific optimizations
spring.jpa.properties.hibernate.jdbc.batch_size=20
spring.jpa.properties.hibernate.order_inserts=true
spring.jpa.properties.hibernate.order_updates=true
spring.jpa.properties.hibernate.jdbc.batch_versioned_data=true
```

---

## 🎯 Testing PostgreSQL Connection

### Test 1: Verify Connection

```bash
# Test PostgreSQL is running
psql -U postgres -c "SELECT version();"
```

### Test 2: Check Database

```bash
# List all databases
psql -U postgres -c "\l"

# Should see erp_payroll_db in the list
```

### Test 3: Test Application Connection

1. Start the application: `.\mvnw spring-boot:run`
2. Check console for connection messages
3. Look for: `HikariPool-1 - Start completed`

### Test 4: Verify Tables

```sql
-- Connect to database
psql -U postgres -d erp_payroll_db

-- Count employees (should be 3 after first run)
SELECT COUNT(*) FROM employees;

-- Check deductions (should be 6)
SELECT deduction_name, percentage FROM deductions;
```

---

## 📊 PostgreSQL vs MySQL Differences

### Data Types
| MySQL | PostgreSQL | Notes |
|-------|------------|-------|
| `BIGINT AUTO_INCREMENT` | `BIGSERIAL` | Auto-handled by JPA |
| `VARCHAR(n)` | `VARCHAR(n)` | Same |
| `TEXT` | `TEXT` | Same |
| `DECIMAL(10,2)` | `NUMERIC(10,2)` | Same in practice |
| `DATETIME` | `TIMESTAMP` | Auto-handled by JPA |

### SQL Commands
| Operation | MySQL | PostgreSQL |
|-----------|-------|------------|
| Use database | `USE db;` | `\c db` |
| Show tables | `SHOW TABLES;` | `\dt` |
| Describe table | `DESCRIBE table;` | `\d table` |
| Show functions | `SHOW PROCEDURE STATUS;` | `\df` |
| Exit | `EXIT;` | `\q` |

### String Concatenation
```sql
-- MySQL
CONCAT('Hello', ' ', 'World')

-- PostgreSQL
'Hello' || ' ' || 'World'
```

### Current Timestamp
```sql
-- MySQL
NOW()

-- PostgreSQL
CURRENT_TIMESTAMP or NOW()
```

---

## 🐛 Troubleshooting

### Issue 1: "Connection refused" Error

**Solution:**
```bash
# Check if PostgreSQL is running
sudo systemctl status postgresql  # Linux
brew services list                 # Mac

# Start PostgreSQL if not running
sudo systemctl start postgresql    # Linux
brew services start postgresql     # Mac

# Windows: Check Services app for "postgresql" service
```

### Issue 2: "password authentication failed"

**Solution:**
1. Verify password in `application.properties`
2. Reset PostgreSQL password:
```bash
# Linux/Mac
sudo -u postgres psql
ALTER USER postgres PASSWORD 'newpassword';
\q

# Then update application.properties
```

### Issue 3: "database does not exist"

**Solution:**
```bash
# Create the database
psql -U postgres -c "CREATE DATABASE erp_payroll_db;"
```

### Issue 4: "Port 5432 already in use"

**Solution:**
Either change PostgreSQL port or stop other service using 5432:
```bash
# Find process using port 5432
netstat -ano | findstr :5432    # Windows
lsof -i :5432                   # Linux/Mac

# Kill the process or change port in postgresql.conf
```

### Issue 5: Trigger not firing

**Solution:**
```sql
-- Reinstall triggers
\c erp_payroll_db
\i database-triggers.sql

-- Or manually execute the SQL file content
```

---

## ✅ Verification Checklist

After setup, verify:

- [ ] PostgreSQL is running (port 5432)
- [ ] Database `erp_payroll_db` exists
- [ ] Application connects successfully
- [ ] 6 tables created (employees, users, employments, deductions, payslips, messages)
- [ ] 3 functions created (send_payslip_message, process_all_approved_payslips, trigger function)
- [ ] 1 trigger created (after_payslip_status_update)
- [ ] Test data loaded (3 employees, 6 deductions)
- [ ] Swagger UI accessible (http://localhost:8080/swagger-ui.html)
- [ ] Login works with test accounts
- [ ] Can generate payroll
- [ ] Can approve payslip (trigger fires)
- [ ] Message appears in messages table

---

## 🎓 PostgreSQL Advantages for This Project

### 1. **Better Standards Compliance**
- More ANSI SQL compliant
- Trigger syntax more standard

### 2. **Advanced Features**
- Better function/procedure support
- More data types (JSON, Arrays, etc.)
- Better full-text search

### 3. **Open Source**
- Completely free (no licensing concerns)
- Large community
- Extensive documentation

### 4. **Performance**
- Better handling of complex queries
- Advanced indexing options
- Better concurrent access

### 5. **Reliability**
- ACID compliant
- Strong data integrity
- Better crash recovery

---

## 📚 Useful PostgreSQL Commands

### Database Operations
```sql
-- List all databases
\l

-- Connect to database
\c erp_payroll_db

-- Show current database
SELECT current_database();

-- Show database size
SELECT pg_size_pretty(pg_database_size('erp_payroll_db'));
```

### Table Operations
```sql
-- List tables
\dt

-- Describe table
\d employees

-- Show table size
SELECT pg_size_pretty(pg_total_relation_size('employees'));

-- Count rows
SELECT COUNT(*) FROM employees;
```

### Function/Trigger Operations
```sql
-- List functions
\df

-- Show function definition
\sf send_payslip_message

-- List triggers
SELECT * FROM information_schema.triggers;

-- Drop and recreate trigger
DROP TRIGGER IF EXISTS after_payslip_status_update ON payslips;
-- Then run the CREATE TRIGGER command
```

### Monitoring
```sql
-- Show active connections
SELECT * FROM pg_stat_activity WHERE datname = 'erp_payroll_db';

-- Show table statistics
SELECT * FROM pg_stat_user_tables;

-- Show index usage
SELECT * FROM pg_stat_user_indexes;
```

---

## 🔗 Useful Resources

- **PostgreSQL Official Docs**: https://www.postgresql.org/docs/
- **PostgreSQL Tutorial**: https://www.postgresqltutorial.com/
- **pgAdmin Download**: https://www.pgadmin.org/download/
- **Spring Boot + PostgreSQL**: https://spring.io/guides/gs/accessing-data-postgresql/

---

## 🎯 Next Steps

1. ✅ PostgreSQL installed and running
2. ✅ Database created
3. ✅ Application configured
4. ✅ Application running
5. ✅ Triggers installed
6. ✅ Data loaded
7. ✅ Ready to test!

**Proceed to**: `EXAM_CHECKLIST.md` for exam preparation

---

**🎓 Your ERP Payroll Management System is now running on PostgreSQL! 🎓**

*Last Updated: June 5, 2026*  
*PostgreSQL Version: 14+ recommended*  
*Status: Production Ready*
