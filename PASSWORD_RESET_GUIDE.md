# Password Reset Guide

## Overview

The ERP Payroll System includes a secure email-based password recovery system that allows users to reset their passwords when they forget them. The system uses secure UUID tokens sent via email to verify the user's identity before allowing password changes.

---

## Table of Contents

1. [Features](#features)
2. [How It Works](#how-it-works)
3. [Database Schema](#database-schema)
4. [API Endpoints](#api-endpoints)
5. [Security Features](#security-features)
6. [Password Requirements](#password-requirements)
7. [Email Configuration](#email-configuration)
8. [Usage Examples](#usage-examples)
9. [Testing Guide](#testing-guide)
10. [Troubleshooting](#troubleshooting)

---

## Features

- **Email-Based Recovery**: Secure password reset via email verification
- **UUID Tokens**: Cryptographically secure unique tokens for each reset request
- **Time-Limited**: Tokens expire after 1 hour for security
- **One-Time Use**: Each token can only be used once
- **Password Validation**: Strong password requirements enforced
- **Auto-Cleanup**: Expired tokens automatically removed
- **Email Confirmation**: Users receive confirmation after successful reset
- **User-Friendly**: Clear error messages and status feedback

---

## How It Works

### 1. User Requests Reset

```
User forgets password → Enters email → Requests reset link
```

### 2. System Generates Token

```
System validates email → Generates UUID token → Creates expiry time → Saves to database
```

### 3. Email Sent

```
System sends email with reset link → User clicks link → Opens reset page
```

### 4. Password Reset

```
User enters new password → Submits with token → System validates → Updates password
```

### 5. Confirmation

```
System marks token as used → Sends confirmation email → User can login
```

---

## Database Schema

### Password Reset Token Table

```sql
CREATE TABLE password_reset_tokens (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    employee_id BIGINT NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    used BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_employee FOREIGN KEY (employee_id) REFERENCES employees(id)
);

-- Index for fast token lookup
CREATE INDEX idx_token ON password_reset_tokens(token);
CREATE INDEX idx_expires_at ON password_reset_tokens(expires_at);
```

### Entity Relationships

```
Employee (1) ←→ (Many) PasswordResetToken
```

- One employee can have multiple reset tokens (historical)
- Only one active (unused) token at a time

---

## API Endpoints

### 1. Request Password Reset

**POST** `/api/v1/password/forgot`

Request a password reset link to be sent via email.

#### Request Body

```json
{
  "email": "employee@example.com"
}
```

#### Success Response (200 OK)

```json
{
  "success": true,
  "message": "Password reset link sent to your email",
  "data": "Please check your email to reset your password. Link expires in 1 hour(s)."
}
```

#### Error Responses

```json
// Email not found (404)
{
  "success": false,
  "message": "No account found with email: employee@example.com"
}

// Email service failure (400)
{
  "success": false,
  "message": "Failed to send reset email. Please try again later."
}
```

---

### 2. Reset Password

**POST** `/api/v1/password/reset`

Reset password using the token received via email.

#### Request Body

```json
{
  "token": "550e8400-e29b-41d4-a716-446655440000",
  "newPassword": "NewSecure@Pass123",
  "confirmPassword": "NewSecure@Pass123"
}
```

#### Success Response (200 OK)

```json
{
  "success": true,
  "message": "Password reset successfully",
  "data": "You can now login with your new password"
}
```

#### Error Responses

```json
// Passwords don't match (400)
{
  "success": false,
  "message": "Passwords do not match"
}

// Invalid/expired token (400)
{
  "success": false,
  "message": "Invalid or expired reset token"
}

// Token expired (400)
{
  "success": false,
  "message": "Reset token has expired. Please request a new one."
}

// Weak password (400)
{
  "success": false,
  "message": "Password must be at least 8 characters long and contain uppercase, lowercase, digit, and special character"
}
```

---

### 3. Validate Reset Token

**GET** `/api/v1/password/validate-token?token=YOUR_TOKEN`

Check if a password reset token is still valid.

#### Query Parameters

- `token` (required): The reset token to validate

#### Success Response (200 OK)

```json
{
  "success": true,
  "message": "Token is valid",
  "data": "You can proceed with password reset"
}
```

#### Error Responses

```json
// Invalid/expired token (400)
{
  "success": false,
  "message": "Invalid or expired reset token"
}

// Token expired (400)
{
  "success": false,
  "message": "Reset token has expired. Please request a new one."
}
```

---

## Security Features

### 1. Token Generation

- **UUID Format**: Uses `UUID.randomUUID()` for cryptographically secure tokens
- **Uniqueness**: Database constraint ensures no duplicate tokens
- **Unpredictability**: 128-bit random tokens (e.g., `550e8400-e29b-41d4-a716-446655440000`)

### 2. Token Expiration

- **Time-Limited**: Tokens expire after 1 hour
- **Auto-Delete**: Expired tokens automatically cleaned up every hour
- **Validation**: System checks expiry before allowing reset

### 3. One-Time Use

- **Single Use**: Token marked as "used" after successful reset
- **Cannot Reuse**: Used tokens rejected even if not expired
- **Old Tokens Deleted**: New request deletes previous tokens for same user

### 4. Password Security

- **Encrypted Storage**: Passwords hashed using BCrypt before storage
- **Strong Requirements**: Minimum 8 characters with complexity rules
- **Validation**: Both frontend and backend validation

### 5. Email Security

- **Account Verification**: Reset only sent to registered email addresses
- **Secure Links**: HTTPS recommended for production
- **No Password in Email**: Only secure token sent, never passwords

---

## Password Requirements

### Validation Rules

1. **Minimum Length**: At least 8 characters
2. **Uppercase Letter**: At least one uppercase letter (A-Z)
3. **Lowercase Letter**: At least one lowercase letter (a-z)
4. **Digit**: At least one number (0-9)
5. **Special Character**: At least one special character (@$!%*?&)

### Valid Password Examples

```
✓ SecurePass@123
✓ MyP@ssw0rd!
✓ Admin$2024Strong
✓ Employee#Pass99
```

### Invalid Password Examples

```
✗ password         (no uppercase, digit, or special char)
✗ Password         (no digit or special char)
✗ Password123      (no special char)
✗ Pass@1           (too short, less than 8 characters)
```

### Regular Expression

```java
^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$
```

---

## Email Configuration

### Gmail Setup (Recommended for Development)

1. **Enable 2-Factor Authentication** on your Gmail account
2. **Generate App Password**:
   - Go to Google Account → Security
   - Select "2-Step Verification"
   - Scroll to "App passwords"
   - Generate password for "Mail"

3. **Update `application.properties`**:

```properties
# Mail Configuration
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your_email@gmail.com
spring.mail.password=your_16_digit_app_password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

### Other Email Providers

#### Outlook/Hotmail

```properties
spring.mail.host=smtp-mail.outlook.com
spring.mail.port=587
spring.mail.username=your_email@outlook.com
spring.mail.password=your_password
```

#### Yahoo Mail

```properties
spring.mail.host=smtp.mail.yahoo.com
spring.mail.port=587
spring.mail.username=your_email@yahoo.com
spring.mail.password=your_app_password
```

### Frontend URL Configuration

Update the frontend URL in `application.properties`:

```properties
# Application Configuration
app.frontend.url=http://localhost:3000    # For React/Vue/Angular
# app.frontend.url=https://yourdomain.com  # For production
```

---

## Usage Examples

### Example 1: Complete Password Reset Flow

#### Step 1: Request Reset

```bash
curl -X POST http://localhost:8080/api/v1/password/forgot \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.doe@example.com"
  }'
```

**Response:**
```json
{
  "success": true,
  "message": "Password reset link sent to your email",
  "data": "Please check your email to reset your password. Link expires in 1 hour(s)."
}
```

#### Step 2: Check Email

User receives email with content:

```
Subject: Password Reset Request - ERP Payroll System

Dear John,

We received a request to reset your password for your ERP Payroll System account.

To reset your password, please click the link below:
http://localhost:8080/reset-password?token=550e8400-e29b-41d4-a716-446655440000

This link will expire in 1 hour(s).

If you didn't request this password reset, please ignore this email.
Your password will remain unchanged.

For security reasons, please do not share this link with anyone.

Best regards,
ERP Payroll System Team
```

#### Step 3: Validate Token (Optional)

```bash
curl -X GET "http://localhost:8080/api/v1/password/validate-token?token=550e8400-e29b-41d4-a716-446655440000"
```

**Response:**
```json
{
  "success": true,
  "message": "Token is valid",
  "data": "You can proceed with password reset"
}
```

#### Step 4: Reset Password

```bash
curl -X POST http://localhost:8080/api/v1/password/reset \
  -H "Content-Type: application/json" \
  -d '{
    "token": "550e8400-e29b-41d4-a716-446655440000",
    "newPassword": "NewSecure@Pass123",
    "confirmPassword": "NewSecure@Pass123"
  }'
```

**Response:**
```json
{
  "success": true,
  "message": "Password reset successfully",
  "data": "You can now login with your new password"
}
```

#### Step 5: Confirmation Email

User receives confirmation:

```
Subject: Password Changed Successfully - ERP Payroll System

Dear John,

Your password has been successfully changed.

If you didn't make this change, please contact support immediately.

Best regards,
ERP Payroll System Team
```

---

### Example 2: Using Postman

#### Request Reset

1. Method: **POST**
2. URL: `http://localhost:8080/api/v1/password/forgot`
3. Headers:
   - `Content-Type: application/json`
4. Body (raw JSON):
```json
{
  "email": "employee@example.com"
}
```

#### Reset Password

1. Method: **POST**
2. URL: `http://localhost:8080/api/v1/password/reset`
3. Headers:
   - `Content-Type: application/json`
4. Body (raw JSON):
```json
{
  "token": "your-token-from-email",
  "newPassword": "NewPass@123",
  "confirmPassword": "NewPass@123"
}
```

---

## Testing Guide

### Unit Testing

```java
@Test
void testForgotPassword_Success() {
    // Given
    ForgotPasswordRequest request = new ForgotPasswordRequest();
    request.setEmail("john.doe@example.com");
    
    Employee employee = new Employee();
    employee.setEmail("john.doe@example.com");
    
    when(employeeRepository.findByEmail(anyString()))
        .thenReturn(Optional.of(employee));
    
    // When
    ApiResponse<String> response = passwordResetService.forgotPassword(request);
    
    // Then
    assertTrue(response.isSuccess());
    verify(emailService, times(1)).sendEmail(anyString(), anyString(), anyString());
}

@Test
void testResetPassword_Success() {
    // Given
    ResetPasswordRequest request = new ResetPasswordRequest();
    request.setToken("valid-token");
    request.setNewPassword("NewPass@123");
    request.setConfirmPassword("NewPass@123");
    
    Employee employee = new Employee();
    PasswordResetToken token = PasswordResetToken.builder()
        .token("valid-token")
        .employee(employee)
        .expiresAt(LocalDateTime.now().plusHours(1))
        .used(false)
        .build();
    
    when(tokenRepository.findByTokenAndUsedFalse(anyString()))
        .thenReturn(Optional.of(token));
    
    // When
    ApiResponse<String> response = passwordResetService.resetPassword(request);
    
    // Then
    assertTrue(response.isSuccess());
    assertTrue(token.isUsed());
}
```

### Integration Testing

```bash
# Test forgot password endpoint
curl -X POST http://localhost:8080/api/v1/password/forgot \
  -H "Content-Type: application/json" \
  -d '{"email": "admin@rca.ac.rw"}'

# Check database for token
psql -U postgres -d erp_payroll_db -c "SELECT * FROM password_reset_tokens WHERE employee_id = 1;"

# Test reset password
curl -X POST http://localhost:8080/api/v1/password/reset \
  -H "Content-Type: application/json" \
  -d '{
    "token": "YOUR_TOKEN_HERE",
    "newPassword": "NewAdmin@123",
    "confirmPassword": "NewAdmin@123"
  }'

# Test login with new password
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin@rca.ac.rw",
    "password": "NewAdmin@123"
  }'
```

---

## Troubleshooting

### Issue 1: Email Not Sent

**Symptom**: Reset request succeeds but email never arrives

**Solutions**:
1. Check spam/junk folder
2. Verify email configuration in `application.properties`
3. For Gmail: Ensure app password is used (not regular password)
4. Check logs for email errors:
   ```bash
   tail -f logs/application.log | grep "Failed to send"
   ```

### Issue 2: Token Expired

**Symptom**: "Reset token has expired" error

**Solutions**:
1. Request a new reset link (tokens expire after 1 hour)
2. Check system time is correct
3. Verify token expiry time in database:
   ```sql
   SELECT token, expires_at, created_at FROM password_reset_tokens;
   ```

### Issue 3: Invalid Token

**Symptom**: "Invalid or expired reset token" error

**Causes**:
- Token already used
- Token doesn't exist
- Typo in token
- Token manually deleted from database

**Solutions**:
1. Request a new reset link
2. Check token in database:
   ```sql
   SELECT * FROM password_reset_tokens WHERE token = 'your-token';
   ```

### Issue 4: Password Validation Fails

**Symptom**: "Password must be at least 8 characters..." error

**Solution**: Ensure password meets all requirements:
- At least 8 characters
- One uppercase letter
- One lowercase letter
- One digit
- One special character (@$!%*?&)

### Issue 5: Passwords Don't Match

**Symptom**: "Passwords do not match" error

**Solution**: Ensure `newPassword` and `confirmPassword` are exactly the same

### Issue 6: Account Not Found

**Symptom**: "No account found with email" error

**Solutions**:
1. Verify email is correct
2. Check if account exists:
   ```sql
   SELECT * FROM employees WHERE email = 'email@example.com';
   ```
3. Ensure email matches exactly (case-sensitive)

---

## Best Practices

### For Developers

1. **Never Log Tokens**: Don't log reset tokens in production
2. **Use HTTPS**: Always use HTTPS in production
3. **Rate Limiting**: Implement rate limiting on forgot password endpoint
4. **Monitor Usage**: Track password reset attempts
5. **Audit Trail**: Log password changes for security auditing

### For Users

1. **Check Email**: Always check spam folder
2. **Act Quickly**: Use reset link within 1 hour
3. **Use Strong Password**: Follow password requirements
4. **Secure Email**: Keep email account secure
5. **Report Suspicious Activity**: Report unrequested resets

### For Production

1. **Configure Real SMTP**: Use production email service
2. **Set Frontend URL**: Update `app.frontend.url` to production domain
3. **Enable HTTPS**: Ensure secure communication
4. **Monitor Logs**: Watch for suspicious patterns
5. **Backup Database**: Regular backups including tokens table

---

## Architecture

### Components

```
┌─────────────────┐
│  User/Frontend  │
└────────┬────────┘
         │ HTTP Requests
         ↓
┌─────────────────────────┐
│  PasswordResetController│
└────────┬────────────────┘
         │
         ↓
┌─────────────────────────┐
│ PasswordResetServiceImpl│
└────┬───────────┬────────┘
     │           │
     ↓           ↓
┌────────┐  ┌─────────┐
│  Token │  │  Email  │
│  Repo  │  │ Service │
└────────┘  └─────────┘
```

### Data Flow

```
1. User Request → Controller → Service
2. Service → Find Employee → Generate Token
3. Service → Save Token → Repository
4. Service → Send Email → EmailService
5. User → Click Link → Enter New Password
6. Controller → Service → Validate Token
7. Service → Update Password → Save Employee
8. Service → Mark Token Used → Send Confirmation
```

---

## Statistics

- **Total Endpoints**: 3
- **HTTP Methods**: POST (2), GET (1)
- **Java Classes**: 7
  - 1 Entity (PasswordResetToken)
  - 1 Repository (PasswordResetTokenRepository)
  - 3 DTOs (ForgotPasswordRequest, ResetPasswordRequest, ValidateTokenResponse)
  - 1 Service Interface (PasswordResetService)
  - 1 Service Implementation (PasswordResetServiceImpl)
  - 1 Controller (PasswordResetController)
- **Token Expiry**: 1 hour
- **Auto-Cleanup**: Every hour
- **Token Format**: UUID (128-bit)

---

## Related Documentation

- [OTP Guide](OTP_GUIDE.md) - OTP authentication system
- [API Testing Guide](API_TESTING_GUIDE.md) - Complete API documentation
- [Quick Start Guide](QUICKSTART.md) - Getting started with the system
- [Architecture](ARCHITECTURE.md) - System architecture overview

---

## Support

For issues or questions:
- Check the [Troubleshooting](#troubleshooting) section
- Review application logs: `logs/application.log`
- Check database state: Connect to PostgreSQL and inspect tables
- Contact system administrator

---

**Last Updated**: June 5, 2026  
**Version**: 1.0.0  
**Author**: ERP Payroll System Team
