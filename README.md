# TaskNode API Documentation

> Base URL: `http://localhost:8080`  
> Swagger UI: `http://localhost:8080/swagger-ui.html`  
> Authentication: Bearer JWT Token (in `Authorization` header)

---

## Paging & Collection Endpoints

All API endpoints returning collections (lists of entities) support [Spring Pageable parameters](https://docs.spring.io/spring-data/rest/docs/current/reference/html/#paging-and-sorting):

- `page` (default: 0)
- `size` (default: 10)
- `sort` (example: `createdAt,desc`)

**Response format:**
```json
{
  "content": [],
  "number": 0,
  "size": 10,
  "totalElements": 23,
  "totalPages": 3
}
```
> All previous endpoints returning List have been updated to return Page<T> and accept Pageable parameters.

---

## 1. Authentication

[... giữ các mục khác nguyên ...]

### 4.8 List Project Members

`GET /api/v1/projects/{projectId}/members?page=0&size=10`

**Response: `200 OK`** — `Page<ProjectMemberResponse>`
```json
{
  "content": [
    {
      "id": 1,
      "projectId": 1,
      "userId": 2,
      "userName": "string",
      "roleInProject": "MEMBER",
      "createdAt": "2026-04-23T10:00:00"
    }
  ],
  "number": 0,
  "size": 10,
  "totalElements": 3,
  "totalPages": 1
}
```

---

## 6. Comments

### 6.1 Get Comments of Task

`GET /api/v1/tasks/{taskId}/comments?userId={currentUserId}&page=0&size=10`

> User must be a member of the task's project.

**Response: `200 OK`** — `Page<CommentResponse>`
```json
{
  "content": [
    {
      "id": 1,
      "userId": 2,
      "userName": "string",
      "userAvatarUrl": "string",
      "content": "string",
      "createdAt": "2026-04-23T10:00:00"
    }
  ],
  "number": 0,
  "size": 10,
  "totalElements": 5,
  "totalPages": 1
}
```

---

## 9. Reports

### 9.1 Project Progress

`GET /api/v1/reports/project-progress?page=0&size=10`

**Response: `200 OK`** — `Page<ProjectProgressResponse>`
```json
{
  "content": [
    {
      "projectId": 1,
      "projectName": "string",
      "totalTasks": 20,
      "completedTasks": 12,
      "inProgressTasks": 5,
      "overdueTasks": 2,
      "percentCompleted": 60.0
    }
  ],
  "number": 0,
  "size": 10,
  "totalElements": 2,
  "totalPages": 1
}
```

### 9.2 Employee Performance

`GET /api/v1/reports/employee-performance?start=2026-01-01T00:00:00&end=2026-12-31T23:59:59&page=0&size=10`

**Response: `200 OK`** — `Page<EmployeePerformanceResponse>`
```json
{
  "content": [
    {
      "userId": 1,
      "fullName": "string",
      "email": "string",
      "departmentName": "string",
      "totalTasks": 15,
      "completedTasks": 10,
      "inProgressTasks": 3,
      "rejectedTasks": 1
    }
  ],
  "number": 0,
  "size": 10,
  "totalElements": 1,
  "totalPages": 1
}
```

---

[... giữ lại các phần khác không liên quan đến paging/rest collection ...]
