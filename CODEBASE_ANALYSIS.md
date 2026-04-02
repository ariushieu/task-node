# TaskNode Project - Codebase Analysis

**Analysis Date:** April 2, 2026
**Project Name:** TaskNode
**Build Tool:** Maven
**Language:** Java 17
**Spring Boot Version:** 4.0.5

---

## 1. PROJECT OVERVIEW

### 1.1 Project Setup
- **Group ID:** `learning`
- **Artifact ID:** `TaskNode`
- **Version:** `0.0.1-SNAPSHOT`
- **Package Root:** `learning.tasknode`
- **Java Version:** 17

### 1.2 Key Dependencies
- **Spring Boot Data JPA** - ORM with Hibernate
- **Spring Security** - Authentication & Authorization
- **Spring Web MVC** - REST API development
- **MySQL Connector** - Database driver (MySQL)
- **Lombok** - Code generation for boilerplate
- **Spring Boot DevTools** - Hot reload during development

### 1.3 Database Configuration
**Current Status:** Minimal configuration in `application.properties`
- Only `spring.application.name=TaskNode` is set
- **No JPA/Hibernate configuration** yet
- **No database connection settings** yet

---

## 2. PROJECT STRUCTURE

The project follows standard Spring Boot layered architecture:

```
/e/TaskNode/
├── pom.xml                          # Maven build configuration
├── CLAUDE.md                        # AI working guidelines
├── CODEBASE_ANALYSIS.md            # This file
├── src/
│   ├── main/
│   │   ├── java/learning/tasknode/
│   │   │   ├── TaskNodeApplication.java     # Main Spring Boot app
│   │   │   ├── config/              # Configuration classes
│   │   │   ├── controller/          # REST controllers
│   │   │   ├── dto/                 # DTO classes
│   │   │   ├── enums/               # Enums
│   │   │   ├── exception/           # Exception classes
│   │   │   ├── interfaces/          # Interfaces
│   │   │   ├── mapper/              # DTO mappers
│   │   │   ├── model/               # JPA entities
│   │   │   ├── repository/          # Spring Data repos
│   │   │   └── service/             # Business logic
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── static/
│   │       └── templates/
│   └── test/
└── target/
```

---

## 3. CURRENT STATE

### 3.1 Existing Code
Only one file exists: `TaskNodeApplication.java`

### 3.2 All Directories Status
- **config/** - EMPTY
- **controller/** - EMPTY  
- **dto/** - EMPTY
- **enums/** - EMPTY
- **exception/** - EMPTY
- **interfaces/** - EMPTY
- **mapper/** - EMPTY
- **model/** - EMPTY
- **repository/** - EMPTY
- **service/** - EMPTY

---

## 4. PACKAGE NAMING CONVENTION

Root package: `learning.tasknode`

Sub-packages by layer:
- `learning.tasknode.config` - Configuration classes
- `learning.tasknode.controller` - REST controllers
- `learning.tasknode.service` - Business logic
- `learning.tasknode.repository` - Data access
- `learning.tasknode.dto` - Data transfer objects
- `learning.tasknode.model` - JPA entities
- `learning.tasknode.mapper` - DTO mappers
- `learning.tasknode.exception` - Custom exceptions
- `learning.tasknode.enums` - Enums
- `learning.tasknode.interfaces` - Interfaces

---

## 5. NAMING CONVENTIONS TO ESTABLISH

### Entities
- Class name: PascalCase (e.g., `User`, `Task`, `Project`)
- Primary Key: `id` (Long type with @GeneratedValue)

### DTOs
- Request DTO: `{Entity}CreateRequest` or `{Entity}UpdateRequest`
- Response DTO: `{Entity}Response`

### Controllers
- Class name: `{Entity}Controller`
- Base path: `/api/v1/{entities}` (plural)

### Services
- Interface: `I{Entity}Service`
- Implementation: `{Entity}Service`

### Repositories
- Class name: `{Entity}Repository`
- Extends: `JpaRepository<{Entity}, Long>`

### Mappers
- Class name: `{Entity}Mapper`

### Enums
- Class name: PascalCase (e.g., `UserRole`, `TaskStatus`)

---

## 6. JPA/HIBERNATE CONFIGURATION STATUS

### Current Configuration
- No JPA-specific configuration yet
- No database connection settings

### Recommended Configuration Properties
Missing from application.properties:
- `spring.datasource.url`
- `spring.datasource.username`
- `spring.datasource.password`
- `spring.datasource.driver-class-name`
- `spring.jpa.hibernate.ddl-auto`
- `spring.jpa.show-sql`
- `spring.jpa.properties.hibernate.format_sql`
- `spring.jpa.properties.hibernate.dialect`
- `spring.jpa.properties.hibernate.naming.physical-strategy`

### Naming Strategy Recommendation
Use snake_case for database columns:
`org.hibernate.boot.model.naming.PhysicalNamingStrategySnakeCaseImpl`

Example: Java `firstName` -> Database `first_name`

---

## 7. RELATIONSHIP MAPPING PATTERNS

When entities are created, the following patterns should be used:

**One-to-Many with bidirectional relationship:**
- Parent side: `@OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)`
- Child side: `@ManyToOne` with `@JoinColumn`

**Many-to-Many:**
- Use `@ManyToMany` and `@JoinTable` for join table

**Many-to-One:**
- Use `@ManyToOne` with `@JoinColumn`

---

## 8. BASE CLASSES & COMMON PATTERNS

### Recommended BaseEntity
All entities should extend a common base:
- `id` (Long, @GeneratedValue)
- `createdAt` (LocalDateTime with @CreationTimestamp)
- `updatedAt` (LocalDateTime with @UpdateTimestamp)

### Lombok Usage
- `@Data` for entities and DTOs
- `@NoArgsConstructor` for JPA
- `@AllArgsConstructor` for builders
- `@Builder` for object creation
- `@Slf4j` for logging

---

## 9. ENUMS

No enums currently defined. Common candidates:
- User roles
- Task statuses
- Priority levels
- Entity states

---

## 10. DEVELOPMENT GUIDELINES

From CLAUDE.md:

1. **Always implement directly in code** over writing analysis
2. **Planning files:** `/plan/` with format `YYYYMMDD-[feature-name].md`
3. **No hard-coded secrets** - Use `.env` and environment substitution
4. **REST conventions:**
   - Plural nouns for endpoints: `/api/v1/users`
   - Use appropriate HTTP methods
   - Consistent response wrapper
5. **DTO & Validation:**
   - Separate Request/Response DTOs
   - Use `@NotNull`, `@Size`, `@Email`, etc.
   - Use `@Valid` on controller parameters
6. **Exception Handling:**
   - Use `@RestControllerAdvice` globally
   - Custom exceptions for business logic
   - Meaningful error messages with HTTP status codes
7. **Security:**
   - Store secrets in `.env`
   - Use environment variable substitution
   - Add `.env` to `.gitignore`

---

## 11. READY FOR IMPLEMENTATION

The project structure is complete and ready for:

1. Creating base entity classes
2. Defining entities and DTOs
3. Creating enums if needed
4. Building mappers
5. Creating repositories
6. Implementing services
7. Building controllers
8. Setting up exception handling
9. Configuring database and JPA properties
10. Implementing security configuration

All directories are created and follow Spring Boot best practices.

