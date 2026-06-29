# API Test Guide for FE

Base URL qua API Gateway:

```text
http://localhost:8080
```

Swagger UI:

```text
http://localhost:8080/swagger-ui.html
```

## Response Format

Tat ca API da chuan hoa theo format:

```json
{
  "code": 200,
  "errorCode": null,
  "message": "success",
  "fieldErrors": null,
  "data": {}
}
```

## Common Headers

Public API:

```http
Content-Type: application/json
```

Protected API:

```http
Content-Type: application/json
Authorization: Bearer <accessToken>
```

Login API nen gui them device id neu FE da co:

```http
X-Device-ID: <uuid>
```

Neu khong gui `X-Device-ID`, BE se tao device moi.

## Status Values

Account status:

| Value | Meaning |
| --- | --- |
| 0 | PENDING_EMAIL_VERIFICATION |
| 1 | ACTIVE |
| 2 | SUSPENDED |
| 3 | LOCKED |
| 4 | DELETED |

User profile status:

| Value | Meaning |
| --- | --- |
| 0 | INACTIVE |
| 1 | ACTIVE |
| 2 | SUSPENDED |
| 4 | DELETED |

Device status:

| Value | Meaning |
| --- | --- |
| 0 | INACTIVE |
| 1 | ACTIVE |
| 2 | BLOCKED |
| 3 | TRUSTED |
| 4 | REMOVED |

## API Groups

### Dashboard Admin APIs

Nhom API nay danh cho man hinh quan tri. Tat ca deu can:

```http
Authorization: Bearer <accessToken>
```

User dashboard:

```http
GET   /api/v1/users
GET   /api/v1/users/{userId}
GET   /api/v1/users/by-account/{accountId}
PUT   /api/v1/users/{userId}
PATCH /api/v1/users/{userId}/status
```

Account dashboard:

```http
GET   /api/v1/auth/admin/accounts
GET   /api/v1/auth/admin/accounts/{accountId}
PATCH /api/v1/auth/admin/accounts/{accountId}/status
GET   /api/v1/auth/admin/accounts/{accountId}/roles
PUT   /api/v1/auth/admin/accounts/{accountId}/roles
GET   /api/v1/auth/admin/accounts/{accountId}/permissions
```

Role dashboard:

```http
GET    /api/v1/auth/admin/roles
POST   /api/v1/auth/admin/roles
PUT    /api/v1/auth/admin/roles/{roleId}
DELETE /api/v1/auth/admin/roles/{roleId}
```

Permission dashboard:

```http
GET    /api/v1/auth/admin/permissions
POST   /api/v1/auth/admin/permissions
PUT    /api/v1/auth/admin/permissions/{permissionId}
DELETE /api/v1/auth/admin/permissions/{permissionId}
GET    /api/v1/auth/admin/roles/{roleId}/permissions
PUT    /api/v1/auth/admin/roles/{roleId}/permissions
```

### System UI APIs

Nhom API nay danh cho giao dien nguoi dung cua he thong hoc tap.

Auth:

```http
POST /api/v1/auth/normal/register
POST /api/v1/auth/normal/confirm
POST /api/v1/auth/login/default
```

Google auth:

```http
GET  /api/v1/auth/google/url
GET  /api/v1/auth/google/callback
POST /api/v1/auth/google/login
POST /api/v1/auth/google/register
```

Token/session:

```http
POST /api/v1/auth/refresh
POST /api/v1/auth/logout
```

Forgot password:

```http
POST /api/v1/auth/forgot-password
POST /api/v1/auth/verify-otp
POST /api/v1/auth/reset-password
```

Current user profile:

```http
GET /api/v1/users/me
PUT /api/v1/users/me
```

Note:

- `/api/v1/users/me` lay user profile theo `accountId` trong JWT.
- FE khong can truyen `userId` cho API `/me`.
- API `/me` can `Authorization: Bearer <accessToken>`.

## Auth Flow

### 1. Register Normal

```http
POST /api/v1/auth/normal/register
```

Request:

```json
{
  "email": "student01@example.com",
  "username": "student01",
  "password": "Password@123",
  "confirmPassword": "Password@123"
}
```

Response:

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

Note:

- Account moi duoc tao voi `status = 0`.
- BE gui OTP qua notification flow.
- User profile chua duoc tao cho den khi confirm OTP thanh cong.

### 2. Confirm Normal Registration

```http
POST /api/v1/auth/normal/confirm
```

Request:

```json
{
  "email": "student01@example.com",
  "otp": "123456"
}
```

Response:

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

Note:

- Account duoc activate sang `status = 1`.
- BE publish event tao user profile sang `user-service`.

### 3. Login Default

```http
POST /api/v1/auth/login/default
```

Headers:

```http
X-Device-ID: <uuid optional>
```

Request:

```json
{
  "username": "student01",
  "password": "Password@123"
}
```

Response:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "accessToken": "<jwt>",
    "refreshToken": "<jwt>",
    "username": "student01",
    "email": "student01@example.com",
    "roles": ["USER"]
  }
}
```

FE can luu:

- `accessToken`
- `refreshToken`
- `X-Device-ID` neu FE quan ly device id rieng

### 4. Refresh Token

```http
POST /api/v1/auth/refresh
```

Request:

```json
{
  "refreshToken": "<refreshToken>"
}
```

Response:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "accessToken": "<newAccessToken>",
    "refreshToken": "<newRefreshToken>",
    "username": "student01",
    "roles": ["USER"]
  }
}
```

Note:

- Refresh token co rotation. Sau khi refresh thanh cong, refresh token cu bi revoke.
- FE phai thay refresh token cu bang refresh token moi.

### 5. Logout

```http
POST /api/v1/auth/logout
```

Request:

```json
{
  "refreshToken": "<refreshToken>"
}
```

Response:

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

## Google Auth Flow

### 1. Get Google OAuth URL

```http
GET /api/v1/auth/google/url
```

Response:

```json
{
  "code": 200,
  "message": "success",
  "data": "https://accounts.google.com/o/oauth2/v2/auth?..."
}
```

### 2. Google Callback

```http
GET /api/v1/auth/google/callback?code=<googleCode>&loginType=google
```

Response:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "idToken": "<googleIdToken>"
  }
}
```

### 3. Login With Google

```http
POST /api/v1/auth/google/login
```

Headers:

```http
X-Device-ID: <uuid optional>
```

Request:

```json
{
  "token": "<googleIdToken>"
}
```

Response:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "accessToken": "<jwt>",
    "refreshToken": "<jwt>",
    "username": "student01",
    "email": "student01@example.com",
    "roles": ["USER"]
  }
}
```

### 4. Register With Google

```http
POST /api/v1/auth/google/register
```

Request:

```json
{
  "tokenId": "<googleIdToken>",
  "username": "student01",
  "password": "Password@123",
  "confirmPassword": "Password@123"
}
```

Response:

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

## Forgot Password Flow

### 1. Forgot Password

```http
POST /api/v1/auth/forgot-password
```

Request:

```json
{
  "email": "student01@example.com"
}
```

Response:

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

Note:

- Neu email khong ton tai, BE van tra success de tranh user enumeration.

### 2. Verify OTP

```http
POST /api/v1/auth/verify-otp
```

Request:

```json
{
  "email": "student01@example.com",
  "otp": "123456"
}
```

Response:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "resetToken": "<reset-token>"
  }
}
```

### 3. Reset Password

```http
POST /api/v1/auth/reset-password
```

Request:

```json
{
  "resetToken": "<reset-token>",
  "newPassword": "NewPassword@123",
  "confirmPassword": "NewPassword@123"
}
```

Response:

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

Note:

- Sau khi reset password, BE revoke tat ca refresh token cua account.
- FE nen logout user khoi tat ca tab/session hien tai.

## User Dashboard APIs

Tat ca API trong section nay can:

```http
Authorization: Bearer <accessToken>
```

Role yeu cau:

- Read: `ADMIN`, `MANAGER`, `SUPPORT`
- Update status / role / permission: `ADMIN`

### 1. Search Users

```http
GET /api/v1/users?keyword=student&status=1&page=0&size=10&sort=insertedAt,desc
```

Query params:

| Param | Required | Description |
| --- | --- | --- |
| keyword | No | Search email, username, firstName, lastName |
| status | No | User profile status int |
| page | No | Default 0 |
| size | No | Default 10 |
| sort | No | Example `insertedAt,desc` |

Response:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "content": [
      {
        "id": "user-uuid",
        "accountId": "account-uuid",
        "username": "student01",
        "email": "student01@example.com",
        "firstName": "Van A",
        "lastName": "Nguyen",
        "address": "Ha Noi",
        "phoneNumber": "0900000000",
        "status": 1,
        "insertedAt": "2026-06-25T10:00:00Z"
      }
    ],
    "totalElements": 1,
    "totalPages": 1,
    "size": 10,
    "number": 0
  }
}
```

### 2. Get User Detail

```http
GET /api/v1/users/{userId}
```

### 3. Get User By Account ID

```http
GET /api/v1/users/by-account/{accountId}
```

### 4. Update User Profile

```http
PUT /api/v1/users/{userId}
```

Request:

```json
{
  "firstName": "Van A",
  "lastName": "Nguyen",
  "address": "Ha Noi",
  "phoneNumber": "0900000000"
}
```

### 5. Update User Status

```http
PATCH /api/v1/users/{userId}/status
```

Role: `ADMIN`

Request:

```json
{
  "status": 2
}
```

## Current User Profile APIs

Nhom API nay danh cho giao dien nguoi dung tu xem/sua profile cua chinh minh.

### 1. Get My Profile

```http
GET /api/v1/users/me
```

Headers:

```http
Authorization: Bearer <accessToken>
```

Response:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": "user-uuid",
    "accountId": "account-uuid",
    "username": "student01",
    "email": "student01@example.com",
    "firstName": "Van A",
    "lastName": "Nguyen",
    "address": "Ha Noi",
    "phoneNumber": "0900000000",
    "status": 1,
    "insertedAt": "2026-06-25T10:00:00Z"
  }
}
```

### 2. Update My Profile

```http
PUT /api/v1/users/me
```

Headers:

```http
Authorization: Bearer <accessToken>
Content-Type: application/json
```

Request:

```json
{
  "firstName": "Van A",
  "lastName": "Nguyen",
  "address": "Ha Noi",
  "phoneNumber": "0900000000"
}
```

Response:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": "user-uuid",
    "accountId": "account-uuid",
    "username": "student01",
    "email": "student01@example.com",
    "firstName": "Van A",
    "lastName": "Nguyen",
    "address": "Ha Noi",
    "phoneNumber": "0900000000",
    "status": 1,
    "insertedAt": "2026-06-25T10:00:00Z"
  }
}
```

## Auth Admin APIs

Tat ca API trong section nay can:

```http
Authorization: Bearer <accessToken>
```

### 1. Search Accounts

```http
GET /api/v1/auth/admin/accounts?keyword=student&status=1&page=0&size=20&sort=insertedAt,desc
```

Response data item:

```json
{
  "id": "account-uuid",
  "email": "student01@example.com",
  "username": "student01",
  "status": 1,
  "roles": [
    {
      "id": 1,
      "name": "USER"
    }
  ],
  "permissions": ["LESSON_VIEW"],
  "insertedAt": "2026-06-25T10:00:00Z",
  "updatedAt": "2026-06-25T10:00:00Z"
}
```

### 2. Get Account Detail

```http
GET /api/v1/auth/admin/accounts/{accountId}
```

### 3. Update Account Status

```http
PATCH /api/v1/auth/admin/accounts/{accountId}/status
```

Role: `ADMIN`

Request:

```json
{
  "status": 3
}
```

### 4. Get Account Roles

```http
GET /api/v1/auth/admin/accounts/{accountId}/roles
```

### 5. Replace Account Roles

```http
PUT /api/v1/auth/admin/accounts/{accountId}/roles
```

Role: `ADMIN`

Request:

```json
{
  "roleIds": [1, 2]
}
```

### 6. Get Account Effective Permissions

```http
GET /api/v1/auth/admin/accounts/{accountId}/permissions
```

Response:

```json
{
  "code": 200,
  "message": "success",
  "data": ["USER_VIEW", "LESSON_VIEW"]
}
```

## Role APIs

### 1. Search Roles

```http
GET /api/v1/auth/admin/roles?keyword=admin&page=0&size=50&sort=name,asc
```

### 2. Create Role

```http
POST /api/v1/auth/admin/roles
```

Role: `ADMIN`

Request:

```json
{
  "name": "TEACHER"
}
```

### 3. Update Role

```http
PUT /api/v1/auth/admin/roles/{roleId}
```

Role: `ADMIN`

Request:

```json
{
  "name": "CONTENT_MANAGER"
}
```

### 4. Delete Role

```http
DELETE /api/v1/auth/admin/roles/{roleId}
```

Role: `ADMIN`

## Permission APIs

### 1. Search Permissions

```http
GET /api/v1/auth/admin/permissions?keyword=view&module=LESSON&scope=LESSON&page=0&size=50&sort=code,asc
```

### 2. Create Permission

```http
POST /api/v1/auth/admin/permissions
```

Role: `ADMIN`

Request:

```json
{
  "code": "LESSON_VIEW",
  "name": "View lessons",
  "module": "LESSON",
  "scope": "LESSON",
  "description": "Allow user to view lessons"
}
```

### 3. Update Permission

```http
PUT /api/v1/auth/admin/permissions/{permissionId}
```

Role: `ADMIN`

Request:

```json
{
  "code": "LESSON_VIEW",
  "name": "View lessons",
  "module": "LESSON",
  "scope": "LESSON",
  "description": "Allow user to view lessons"
}
```

### 4. Delete Permission

```http
DELETE /api/v1/auth/admin/permissions/{permissionId}
```

Role: `ADMIN`

### 5. Get Role Permissions

```http
GET /api/v1/auth/admin/roles/{roleId}/permissions
```

### 6. Replace Role Permissions

```http
PUT /api/v1/auth/admin/roles/{roleId}/permissions
```

Role: `ADMIN`

Request:

```json
{
  "permissionIds": [1, 2, 3]
}
```

## FE Suggested Test Order

1. Register normal account.
2. Read OTP from email/log/test channel.
3. Confirm normal registration.
4. Login default.
5. Save access token and refresh token.
6. Call `GET /api/v1/users/by-account/{accountId}` after user-service consumes Kafka event.
7. Login as admin.
8. Search users.
9. Search accounts.
10. Create permissions.
11. Assign permissions to role.
12. Assign role to account.
13. Test refresh token rotation.
14. Test logout.
15. Test forgot password flow.
