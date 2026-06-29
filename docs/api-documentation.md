# API Documentation

Tài liệu này được generate theo controller và DTO hiện có trong source code.

Base URL khi đi qua API Gateway:

```text
http://localhost:8080
```

Swagger UI:

```text
http://localhost:8080/swagger-ui.html
```

OpenAPI JSON:

```text
http://localhost:8080/v3/api-docs/auth-service
http://localhost:8080/v3/api-docs/user-service
```

## Response Chung

Hầu hết API trả về wrapper `ResponseData<T>`:

```json
{
  "code": 200,
  "errorCode": null,
  "message": "success",
  "fieldErrors": null,
  "data": {}
}
```

Với endpoint không trả data:

```json
{
  "code": 200,
  "errorCode": null,
  "message": "success",
  "fieldErrors": null,
  "data": null
}
```

Các API yêu cầu đăng nhập dùng header:

```http
Authorization: Bearer <accessToken>
```

Một số API login dùng thêm header optional:

```http
X-Device-ID: <uuid-or-device-id>
```

## Auth Service

### Login username/password

```http
POST /api/v1/auth/login/default
```

Public.

Request:

```json
{
  "username": "teacher01",
  "password": "password123"
}
```

Response:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "accessToken": "<jwt-access-token>",
    "refreshToken": "<jwt-refresh-token>",
    "username": "teacher01",
    "email": "teacher01@example.com",
    "roles": ["TEACHER"]
  }
}
```

### Register normal account

```http
POST /api/v1/auth/normal/register
```

Public.

Request:

```json
{
  "email": "student01@example.com",
  "username": "student01",
  "password": "password123",
  "confirmPassword": "password123"
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

### Confirm normal registration

```http
POST /api/v1/auth/normal/confirm
```

Public.

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

### Get Google OAuth URL

```http
GET /api/v1/auth/google/url
```

Public.

Response:

```json
{
  "code": 200,
  "message": "success",
  "data": "https://accounts.google.com/o/oauth2/v2/auth?client_id=...&redirect_uri=...&response_type=code&scope=openid%20email%20profile&access_type=offline"
}
```

### Google OAuth callback

```http
GET /api/v1/auth/google/callback?code=<google-code>&loginType=google
```

Public.

Query params:

| Name | Required | Description |
| --- | --- | --- |
| `code` | Yes | Authorization code Google trả về |
| `loginType` | No | Default `google` |

Response:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "idToken": "<google-id-token>"
  }
}
```

### Login with Google token

```http
POST /api/v1/auth/google/login
```

Public.

Request:

```json
{
  "token": "<google-id-token>"
}
```

Response:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "accessToken": "<jwt-access-token>",
    "refreshToken": "<jwt-refresh-token>",
    "username": "student01",
    "email": "student01@example.com",
    "roles": ["USER"]
  }
}
```

### Register with Google token

```http
POST /api/v1/auth/google/register
```

Public.

Request:

```json
{
  "tokenId": "<google-id-token>",
  "username": "student01",
  "password": "password123",
  "confirmPassword": "password123"
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

### Forgot password

```http
POST /api/v1/auth/forgot-password
```

Public.

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

### Verify reset password OTP

```http
POST /api/v1/auth/verify-otp
```

Public.

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

### Reset password

```http
POST /api/v1/auth/reset-password
```

Public.

Request:

```json
{
  "resetToken": "<reset-token>",
  "newPassword": "newPassword123",
  "confirmPassword": "newPassword123"
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

### Refresh token

```http
POST /api/v1/auth/refresh
```

Public.

Request:

```json
{
  "refreshToken": "<refresh-token>"
}
```

Response:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "accessToken": "<new-jwt-access-token>",
    "refreshToken": "<new-refresh-token>",
    "username": "student01",
    "email": "student01@example.com",
    "roles": ["USER"]
  }
}
```

### Logout

```http
POST /api/v1/auth/logout
```

Public theo security config hiện tại.

Request:

```json
{
  "refreshToken": "<refresh-token>"
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

## Auth Admin APIs

Base path:

```text
/api/v1/auth/admin
```

Yêu cầu:

```http
Authorization: Bearer <accessToken>
```

Class-level permission:

```text
hasAnyRole('ADMIN','MANAGER','SUPPORT')
```

Các API ghi/xóa thường yêu cầu thêm `hasRole('ADMIN')`.

### Search accounts

```http
GET /api/v1/auth/admin/accounts?keyword=<keyword>&status=<status>&page=0&size=20&sort=insertedAt
```

Response:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "content": [
      {
        "id": "0f777467-4d0b-4a8c-9a9a-7c7028fd2a1b",
        "email": "student01@example.com",
        "username": "student01",
        "status": 1,
        "roles": [
          {
            "id": 1,
            "name": "STUDENT"
          }
        ],
        "permissions": ["DASHBOARD_VIEW"],
        "insertedAt": "2026-06-28T10:00:00Z",
        "updatedAt": "2026-06-28T10:00:00Z"
      }
    ],
    "pageable": {},
    "totalElements": 1,
    "totalPages": 1,
    "last": true,
    "size": 20,
    "number": 0
  }
}
```

### Get account detail

```http
GET /api/v1/auth/admin/accounts/{accountId}
```

Response data: `AdminAccountResponse`.

### Update account status

```http
PATCH /api/v1/auth/admin/accounts/{accountId}/status
```

Requires `ADMIN`.

Request:

```json
{
  "status": 1
}
```

Response data: `AdminAccountResponse`.

### Get account roles

```http
GET /api/v1/auth/admin/accounts/{accountId}/roles
```

Response:

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "name": "STUDENT"
    }
  ]
}
```

### Replace account roles

```http
PUT /api/v1/auth/admin/accounts/{accountId}/roles
```

Requires `ADMIN`.

Request:

```json
{
  "roleIds": [1, 2]
}
```

Response data: `RoleResponse[]`.

### Get account effective permissions

```http
GET /api/v1/auth/admin/accounts/{accountId}/permissions
```

Response:

```json
{
  "code": 200,
  "message": "success",
  "data": [
    "DASHBOARD_VIEW",
    "CATEGORY_SCREEN_VIEW"
  ]
}
```

### Search roles

```http
GET /api/v1/auth/admin/roles?keyword=<keyword>&page=0&size=50&sort=name
```

Response data: `Page<RoleResponse>`.

### Create role

```http
POST /api/v1/auth/admin/roles
```

Requires `ADMIN`.

Request:

```json
{
  "name": "TEACHER"
}
```

Response:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 2,
    "name": "TEACHER"
  }
}
```

### Update role

```http
PUT /api/v1/auth/admin/roles/{roleId}
```

Requires `ADMIN`.

Request:

```json
{
  "name": "TEACHER"
}
```

Response data: `RoleResponse`.

### Delete role

```http
DELETE /api/v1/auth/admin/roles/{roleId}
```

Requires `ADMIN`.

Response data: `null`.

### Search permissions

```http
GET /api/v1/auth/admin/permissions?keyword=<keyword>&module=<module>&scope=<scope>&page=0&size=50&sort=code
```

Response data: `Page<PermissionResponse>`.

### Create permission

```http
POST /api/v1/auth/admin/permissions
```

Requires `ADMIN`.

Request:

```json
{
  "code": "CATEGORY_SCREEN_VIEW",
  "name": "View category screen",
  "module": "CATEGORY",
  "scope": "SCREEN",
  "description": "Allow user to view category management screen"
}
```

Response:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 10,
    "code": "CATEGORY_SCREEN_VIEW",
    "name": "View category screen",
    "module": "CATEGORY",
    "scope": "SCREEN",
    "description": "Allow user to view category management screen"
  }
}
```

### Update permission

```http
PUT /api/v1/auth/admin/permissions/{permissionId}
```

Requires `ADMIN`.

Request: same as create permission.

Response data: `PermissionResponse`.

### Delete permission

```http
DELETE /api/v1/auth/admin/permissions/{permissionId}
```

Requires `ADMIN`.

Response data: `null`.

### Get role permissions

```http
GET /api/v1/auth/admin/roles/{roleId}/permissions
```

Response data: `PermissionResponse[]`.

### Replace role permissions

```http
PUT /api/v1/auth/admin/roles/{roleId}/permissions
```

Requires `ADMIN`.

Request:

```json
{
  "permissionIds": [1, 2, 3]
}
```

Response data: `PermissionResponse[]`.

## User Service

Base path:

```text
/api/v1/users
```

Yêu cầu:

```http
Authorization: Bearer <accessToken>
```

### Get current user profile

```http
GET /api/v1/users/me
```

Response:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": "d88e86d0-6d56-4492-aaf8-919d6772cfa3",
    "accountId": "0f777467-4d0b-4a8c-9a9a-7c7028fd2a1b",
    "username": "student01",
    "email": "student01@example.com",
    "firstName": "An",
    "lastName": "Nguyen",
    "address": "Ha Noi",
    "phoneNumber": "0900000000",
    "status": 1,
    "insertedAt": "2026-06-28T10:00:00Z"
  }
}
```

### Update current user profile

```http
PUT /api/v1/users/me
```

Request:

```json
{
  "firstName": "An",
  "lastName": "Nguyen",
  "address": "Ha Noi",
  "phoneNumber": "0900000000"
}
```

Response data: `UserResponse`.

### Search users

```http
GET /api/v1/users?keyword=<keyword>&status=<status>&page=0&size=10&sort=insertedAt
```

Requires:

```text
hasAnyRole('ADMIN','MANAGER','SUPPORT')
```

Response data: `Page<UserResponse>`.

### Get user detail

```http
GET /api/v1/users/{userId}
```

Requires:

```text
hasAnyRole('ADMIN','MANAGER','SUPPORT')
```

Response data: `UserResponse`.

### Get user by account id

```http
GET /api/v1/users/by-account/{accountId}
```

Requires:

```text
hasAnyRole('ADMIN','MANAGER','SUPPORT')
```

Response data: `UserResponse`.

### Update user profile

```http
PUT /api/v1/users/{userId}
```

Requires:

```text
hasAnyRole('ADMIN','MANAGER','SUPPORT')
```

Request:

```json
{
  "firstName": "An",
  "lastName": "Nguyen",
  "address": "Ha Noi",
  "phoneNumber": "0900000000"
}
```

Response data: `UserResponse`.

### Update user status

```http
PATCH /api/v1/users/{userId}/status
```

Requires:

```text
hasRole('ADMIN')
```

Request:

```json
{
  "status": 1
}
```

Response data: `UserResponse`.

## Health APIs

Các endpoint dưới đây hiện chỉ trả về chuỗi health check và đều yêu cầu role:

```text
hasAnyRole('ADMIN', 'MANAGER', 'USER')
```

### Documents service

```http
GET /api/v1/documents/health
```

Response:

```json
{
  "code": 200,
  "message": "success",
  "data": "documents-service is running"
}
```

### Elearning service

```http
GET /api/v1/elearning/health
```

Response:

```json
{
  "code": 200,
  "message": "success",
  "data": "elearning-service is running"
}
```

### Report service

```http
GET /api/v1/reports/health
```

Response:

```json
{
  "code": 200,
  "message": "success",
  "data": "report-service is running"
}
```

### Testing service

```http
GET /api/v1/tests/health
```

Response:

```json
{
  "code": 200,
  "message": "success",
  "data": "testing-service is running"
}
```

## Test Permission APIs

Các API này bị `@Hidden` khỏi Swagger nhưng vẫn có trong code.

### Public test

```http
GET /api/v1/test/public
```

Public.

Response:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "endpoint": "public",
    "message": "Anyone can access this endpoint"
  }
}
```

### User test

```http
GET /api/v1/test/user
```

Requires `USER`.

Response:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "endpoint": "user",
    "message": "Access granted - USER role verified",
    "username": "student01"
  }
}
```

### Admin test

```http
GET /api/v1/test/admin
```

Requires `ADMIN`.

Response:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "endpoint": "admin",
    "message": "Access granted - ADMIN role verified",
    "username": "admin01"
  }
}
```

### Admin or user test

```http
GET /api/v1/test/admin-or-user
```

Requires `ADMIN` or `USER`.

Response:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "endpoint": "admin-or-user",
    "message": "Access granted - ADMIN or USER role verified",
    "username": "admin01"
  }
}
```

## Gateway Routes

Các route hiện có trong API Gateway:

| Service | Gateway Path |
| --- | --- |
| auth-service | `/api/v1/auth/**` |
| user-service | `/api/v1/users/**` |
| notification-service | `/api/v1/notifications/**` |
| report-service | `/api/v1/reports/**` |
| documents-service | `/api/v1/documents/**` |
| testing-service | `/api/v1/tests/**` |
| elearning-service | `/api/v1/elearning/**` |
| auth-service test | `/api/v1/test/**` |

Lưu ý: gateway có route `/api/v1/notifications/**`, nhưng hiện không tìm thấy controller notification public trong source code tại thời điểm generate tài liệu này.
