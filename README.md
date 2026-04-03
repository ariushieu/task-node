# TaskNode API Documentation

> Base URL: `http://localhost:8080`  
> Swagger UI: `http://localhost:8080/swagger-ui.html`  
> Authentication: Bearer JWT Token (in `Authorization` header)

---

## 1. Authentication

### 1.1 Login

`POST /api/v1/auth/login`

**Request:**
```json
{
  "username": "string (required)",
  "password": "string (required)"
}
```

**Response: `200 OK`**
```json
{
  "accessToken": "string",
  "refreshToken": "string",
  "tokenType": "Bearer",
  "userId": 1,
  "username": "string",
  "fullName": "string",
  "email": "string",
  "role": "ADMIN"
}
```

### 1.2 Refresh Token

`POST /api/v1/auth/refresh-token`

**Request:**
```json
{
  "refreshToken": "string (required)"
}
```

**Response: `200 OK`**
```json
{
  "accessToken": "string",
  "refreshToken": "string",
  "tokenType": "Bearer"
}
```

---

## Error Responses

### 400 Bad Request (Validation Error)
```json
{
  "timestamp": "2026-04-03T10:00:00",
  "status": 400,
  "error": "Bad Request",
  "fieldErrors": {
    "username": "Username is required",
    "email": "Email must be valid"
  }
}
```

### 401 Unauthorized
```json
{
  "timestamp": "2026-04-03T10:00:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Bad credentials"
}
```

### 403 Forbidden (Token expired)
```json
{
  "timestamp": "2026-04-03T10:00:00",
  "status": 403,
  "error": "Forbidden",
  "message": "Refresh token was expired. Please make a new login request"
}
```
