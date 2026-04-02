# Authentication - Login + JWT + Refresh Token + Seed Admin

## Context
Project nội bộ, không cho phép đăng ký — chỉ admin cấp account. Cần xây dựng hệ thống đăng nhập với JWT access token + refresh token, và seed sẵn account admin khi khởi động lần đầu.

## Existing Code
- **User entity** (`model/User.java`): username, email, password, fullName, phone, avatarUrl, role (UserRole enum), department, isActive. Extends `BaseEntity` (id, createdAt, updatedAt, isDeleted, deletedAt).
- **UserRole enum** (`enums/UserRole.java`): ADMIN, MANAGER, STAFF
- **SecurityConfig** (`config/SecurityConfig.java`): chỉ whitelist Swagger, chưa có JWT filter.
- **Repository layer**: chưa có file nào.
- **Exception layer**: chưa có file nào.
- **JWT config**: `jwt.secret` + `jwt.expiration` đã có trong `application.properties`.

## Plan

### 1. Dependencies — `pom.xml`
- Thêm `jjwt-api`, `jjwt-impl`, `jjwt-jackson` (version 0.12.6)

### 2. Entity bổ sung — `model/RefreshToken.java`
- Fields: `token` (unique), `expiryDate`, quan hệ `@ManyToOne` → `User`
- Extends `BaseEntity`

### 3. Repository — `repository/`
- `UserRepository`: findByUsername, findByEmail, existsByUsername, existsByEmail
- `RefreshTokenRepository`: findByToken, deleteByUser

### 4. DTO — `dto/`
- `LoginRequest`: username, password (có validation)
- `LoginResponse`: accessToken, refreshToken, tokenType, user info
- `RefreshTokenRequest`: refreshToken
- `RefreshTokenResponse`: accessToken, refreshToken, tokenType

### 5. Security / JWT — `config/`
- `JwtTokenProvider`: generateAccessToken, generateRefreshToken, validateToken, getUsernameFromToken
- `JwtAuthenticationFilter` extends `OncePerRequestFilter`: đọc Bearer token từ header, validate, set SecurityContext
- `CustomUserDetailsService` implements `UserDetailsService`: load user từ DB
- `SecurityConfig` (cập nhật): thêm JWT filter, whitelist `/api/v1/auth/**`, cấu hình stateless session, PasswordEncoder bean

### 6. Service — `service/`
- `AuthService`: login (xác thực + trả access/refresh token), refreshToken (validate refresh → trả cặp token mới)
- `RefreshTokenService`: createRefreshToken, verifyExpiration, deleteByUser

### 7. Controller — `controller/AuthController.java`
- `POST /api/v1/auth/login` → LoginRequest → LoginResponse
- `POST /api/v1/auth/refresh-token` → RefreshTokenRequest → RefreshTokenResponse

### 8. Exception — `exception/`
- `TokenRefreshException` (403)
- `GlobalExceptionHandler` (`@RestControllerAdvice`)

### 9. Seed Admin — `config/DataSeeder.java`
- Implements `CommandLineRunner`
- Khi DB chưa có user nào với role ADMIN → tạo admin mặc định:
  - username: `admin`, email: `admin@tasknode.com`, password: `Admin@123` (BCrypt encoded)
  - Log ra console khi seed thành công

### 10. application.properties
- Thêm `jwt.refresh-expiration=604800000` (7 ngày)

## Files tạo mới
1. `model/RefreshToken.java`
2. `repository/UserRepository.java`
3. `repository/RefreshTokenRepository.java`
4. `dto/LoginRequest.java`
5. `dto/LoginResponse.java`
6. `dto/RefreshTokenRequest.java`
7. `dto/RefreshTokenResponse.java`
8. `config/JwtTokenProvider.java`
9. `config/JwtAuthenticationFilter.java`
10. `config/CustomUserDetailsService.java`
11. `config/DataSeeder.java`
12. `service/AuthService.java`
13. `service/RefreshTokenService.java`
14. `controller/AuthController.java`
15. `exception/TokenRefreshException.java`
16. `exception/GlobalExceptionHandler.java`

## Files cập nhật
1. `pom.xml` — thêm jjwt dependencies
2. `config/SecurityConfig.java` — thêm JWT filter, whitelist auth endpoint, PasswordEncoder
3. `application.properties` — thêm jwt.refresh-expiration

## Verification
1. Khởi động app → kiểm tra log "Admin account seeded"
2. `POST /api/v1/auth/login` với `{"username":"admin","password":"Admin@123"}` → nhận accessToken + refreshToken
3. `POST /api/v1/auth/refresh-token` với refreshToken → nhận cặp token mới
4. Gọi API khác không có Bearer token → 401
5. Gọi API khác có Bearer token → 200
