# CLAUDE.md — AI Working Guidelines

## 1. General Principles

- Always prioritize **making changes directly in code** over writing intermediate analysis documents, unless the task is highly complex.
- Avoid creating verbose architecture analysis files or plans unless absolutely necessary for very large tasks.
- **Never hard-code sensitive data** (API keys, passwords, tokens, secrets, etc.). All sensitive values must be stored in `.env` and added to `.gitignore`.

---

## 2. Planning Files

- **Location:** All plan, analysis, and design documents must be saved in the `/plan` directory at the project root. Never use `.claude/plans` or any hidden directory.
- **Naming Convention:** Use the format `YYYYMMDD-[feature-name].md`
  - Example: `20260331-user-authentication.md`
- **Workflow:** Before implementing a large feature, AI must:
  1. Create a plan file under `/plan`.
  2. Wait for the user to review and confirm.
  3. Only begin implementation after approval.

---

## 3. Spring Boot Project Structure

Follow standard layered architecture:

```
src/main/java/com/{project}/
├── controller/       # REST controllers (@RestController)
├── service/          # Business logic (@Service)
├── repository/       # Data access layer (@Repository)
├── entity/           # JPA entities (@Entity)
├── dto/              # Request/Response DTOs
├── mapper/           # DTO <-> Entity mappers
├── config/           # Configuration classes (@Configuration)
├── exception/        # Custom exceptions & global handler
└── util/             # Utility/helper classes
```

---

## 4. Coding Standards

### REST API
- Follow RESTful conventions: `GET`, `POST`, `PUT`, `PATCH`, `DELETE`.
- Use plural nouns for resource paths: `/api/v1/users`, `/api/v1/orders`.
- Return a consistent response wrapper structure across all endpoints.

### DTO & Validation
- Always use separate **Request DTO** and **Response DTO** — never expose JPA entities directly in API responses.
- Apply Bean Validation annotations (`@NotNull`, `@NotBlank`, `@Size`, `@Email`, etc.) on Request DTOs.
- Use `@Valid` in controller method parameters to trigger validation.

### Exception Handling
- Use a centralized `@RestControllerAdvice` class for global exception handling.
- Define custom exceptions (e.g., `ResourceNotFoundException`, `BadRequestException`, `ConflictException`) instead of throwing generic ones.
- Always return meaningful error messages with appropriate HTTP status codes.

### Security & Secrets
- Never hard-code credentials. Use environment variable substitution in `application.yml`:
  ```yaml
  spring:
    datasource:
      password: ${DB_PASSWORD}
  jwt:
    secret: ${JWT_SECRET}
  ```
- Store all secrets in `.env`. Ensure `.env` is listed in `.gitignore`.

---

## 5. Development Workflow

- **Backend first:** Always complete and stabilize Backend logic and APIs before moving to Frontend work.
- **API contract:** Finalize DTOs before implementation so the Frontend can work in parallel if needed.
- **Consistency:** Always verify that Backend DTOs match Frontend interfaces exactly before closing a feature.

---

## 6. File Reading Efficiency

- Avoid reading unnecessary directories: `target/`, `.git/`, `.idea/`, `*.class`, `*.jar`.
- When working on a specific layer (e.g., Service), only read files directly relevant to that layer.
- For Frontend context tasks, only read DTO/Interface files — avoid re-reading full Backend logic when the API contract is already stable.
