# TaskNode API Documentation

> **Base URL:** `http://localhost:8080`  
> **Swagger UI:** `http://localhost:8080/swagger-ui.html`  
> **Authentication:** Bearer JWT Token (in `Authorization` header)

---

## Pagination & Sorting

All endpoints returning collections support Spring Data pagination and sorting:

- `page` (default: 0) - Page number (zero-indexed)
- `size` (default: 10) - Number of items per page
- `sort` (e.g., `createdAt,desc`) - Sort field and direction

**Standard Pageable Response:**

```json
{
  "content":
    /* array of objects */
  ],
  "number": 0,
  "size": 10,
  "totalElements": 23,
  "totalPages": 3,
  "first": true,
  "last": false
}
```

---

## 1. Authentication

### 1.1 Login

`POST /api/v1/auth/login`

**Request Body:**

```json
{
  "username": "string",
  "password": "string"
}
```

**Response: `200 OK`**

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 3600
}
```

### 1.2 Refresh Token

`POST /api/v1/auth/refresh-token`

**Request Body:**

```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Response: `200 OK`**

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 3600
}
```

### 1.3 Logout

`POST /api/v1/auth/logout`

**Headers:**

```
Authorization: Bearer {accessToken}
```

**Response: `204 No Content`**

---

## 2. Users

### 2.1 Create User

`POST /api/v1/users`

**Request Body:**

```json
{
  "username": "string",
  "password": "string",
  "email": "string",
  "fullName": "string",
  "departmentId": 1,
  "role": "STAFF"
}
```

**Response: `200 OK`**

```json
{
  "id": 1,
  "username": "string",
  "email": "string",
  "fullName": "string",
  "departmentId": 1,
  "departmentName": "string",
  "role": "STAFF",
  "createdAt": "2026-04-23T10:00:00",
  "updatedAt": "2026-04-23T10:00:00"
}
```

### 2.2 Get All Users (Pageable)

`GET /api/v1/users?page=0&size=10&sort=createdAt,desc`

**Response: `200 OK`**

```json
{
  "content": [
    {
      "id": 1,
      "username": "string",
      "email": "string",
      "fullName": "string",
      "departmentId": 1,
      "departmentName": "string",
      "role": "STAFF"
    }
  ],
  "number": 0,
  "size": 10,
  "totalElements": 1,
  "totalPages": 1
}
```

### 2.3 Update User

`PUT /api/v1/users/{userId}`

**Request Body:**

```json
{
  "email": "string",
  "fullName": "string",
  "departmentId": 1,
  "role": "MANAGER"
}
```

**Response: `200 OK`** (UserResponse)

### 2.4 Delete User

`DELETE /api/v1/users/{userId}`

**Response: `204 No Content`**

---

## 3. Departments

### 3.1 Create Department

`POST /api/v1/departments`

**Request Body:**

```json
{
  "name": "string",
  "description": "string"
}
```

**Response: `200 OK`**

```json
{
  "id": 1,
  "name": "string",
  "description": "string",
  "createdAt": "2026-04-23T10:00:00"
}
```

### 3.2 Get All Departments (Pageable)

`GET /api/v1/departments?page=0&size=10`

**Response: `200 OK`**

```json
{
  "content": [
    {
      "id": 1,
      "name": "string",
      "description": "string",
      "createdAt": "2026-04-23T10:00:00"
    }
  ],
  "number": 0,
  "size": 10,
  "totalElements": 1,
  "totalPages": 1
}
```

---

## 4. Projects

### 4.1 Create Project

`POST /api/v1/projects`

**Request Body:**

```json
{
  "name": "string",
  "description": "string",
  "startDate": "2026-04-01",
  "endDate": "2026-12-31",
  "managerId": 1
}
```

**Response: `200 OK`**

```json
{
  "id": 1,
  "name": "string",
  "description": "string",
  "startDate": "2026-04-01",
  "endDate": "2026-12-31",
  "managerId": 1,
  "managerName": "string",
  "createdAt": "2026-04-23T10:00:00"
}
```

### 4.2 Get All Projects (Pageable)

`GET /api/v1/projects?page=0&size=10`

**Response: `200 OK`**

```json
{
  "content": [
    {
      "id": 1,
      "name": "string",
      "description": "string",
      "startDate": "2026-04-01",
      "endDate": "2026-12-31",
      "managerId": 1,
      "managerName": "string"
    }
  ],
  "number": 0,
  "size": 10,
  "totalElements": 1,
  "totalPages": 1
}
```

### 4.3 Get Project by ID

`GET /api/v1/projects/{id}`

**Response: `200 OK`** (ProjectResponse)

### 4.4 Update Project

`PUT /api/v1/projects/{id}`

**Request Body:**

```json
{
  "name": "string",
  "description": "string",
  "startDate": "2026-04-01",
  "endDate": "2026-12-31"
}
```

**Response: `200 OK`** (ProjectResponse)

### 4.5 Delete Project

`DELETE /api/v1/projects/{id}`

**Response: `204 No Content`**

### 4.6 Add Member to Project

`POST /api/v1/projects/{projectId}/members`

**Request Body:**

```json
{
  "userId": 1,
  "roleInProject": "MEMBER"
}
```

**Response: `200 OK`**

```json
{
  "id": 1,
  "projectId": 1,
  "userId": 1,
  "userName": "string",
  "roleInProject": "MEMBER"
}
```

### 4.7 Remove Member from Project

`DELETE /api/v1/projects/{projectId}/members/{userId}`

**Response: `204 No Content`**

### 4.8 List Project Members (Pageable)

`GET /api/v1/projects/{projectId}/members?page=0&size=10`

**Response: `200 OK`**

```json
{
  "content": [
    {
      "id": 1,
      "projectId": 1,
      "userId": 1,
      "userName": "string",
      "roleInProject": "MEMBER"
    }
  ],
  "number": 0,
  "size": 10,
  "totalElements": 1,
  "totalPages": 1
}
```

---

## 5. Tasks

### 5.1 Create Task

`POST /api/v1/tasks`

**Request Body:**

```json
{
  "title": "string",
  "description": "string",
  "projectId": 1,
  "assignedToId": 2,
  "priority": "HIGH",
  "dueDate": "2026-05-01T23:59:59",
  "estimatedHours": 8.0
}
```

**Response: `200 OK`**

```json
{
  "id": 1,
  "title": "string",
  "description": "string",
  "projectId": 1,
  "projectName": "string",
  "assignedToId": 2,
  "assignedToName": "string",
  "createdById": 1,
  "createdByName": "string",
  "status": "PENDING",
  "priority": "HIGH",
  "dueDate": "2026-05-01T23:59:59",
  "estimatedHours": 8.0,
  "actualHours": 0.0,
  "createdAt": "2026-04-23T10:00:00",
  "updatedAt": "2026-04-23T10:00:00"
}
```

### 5.2 Get All Tasks (Pageable)

`GET /api/v1/tasks?page=0&size=10&sort=createdAt,desc`

**Response: `200 OK`**

```json
{
  "content": [
    {
      "id": 1,
      "title": "string",
      "description": "string",
      "status": "IN_PROGRESS",
      "priority": "HIGH",
      "dueDate": "2026-05-01T23:59:59"
    }
  ],
  "number": 0,
  "size": 10,
  "totalElements": 1,
  "totalPages": 1
}
```

### 5.3 Get Task by ID

`GET /api/v1/tasks/{id}`

**Response: `200 OK`** (TaskResponse)

### 5.4 Update Task

`PUT /api/v1/tasks/{id}`

**Request Body:**

```json
{
  "title": "string",
  "description": "string",
  "assignedToId": 2,
  "priority": "MEDIUM",
  "dueDate": "2026-05-15T23:59:59",
  "estimatedHours": 10.0
}
```

**Response: `200 OK`** (TaskResponse)

### 5.5 Delete Task

`DELETE /api/v1/tasks/{id}`

**Response: `204 No Content`**

### 5.6 Update Task Status

`PUT /api/v1/tasks/{id}/status`

**Request Body:**

```json
{
  "status": "IN_PROGRESS",
  "actualHours": 5.0
}
```

**Response: `200 OK`** (TaskResponse)

### 5.7 Approve Task

`POST /api/v1/tasks/{id}/approve`

**Response: `200 OK`** (TaskResponse with status = DONE)

### 5.8 Reject Task

`POST /api/v1/tasks/{id}/reject`

**Request Body:**

```json
{
  "reason": "string"
}
```

**Response: `200 OK`** (TaskResponse with status = REJECTED)

### 5.9 Receive Task

`POST /api/v1/tasks/{id}/receive`

**Request Body:**

```json
{
  "userId": 1
}
```

**Response: `200 OK`** (TaskResponse)

---

## 6. Comments

### 6.1 Get Comments of Task (Pageable)

`GET /api/v1/tasks/{taskId}/comments?userId={currentUserId}&page=0&size=10`

**Query Parameters:**

- `userId` (required) - Current user ID
- `page` (optional) - Page number
- `size` (optional) - Page size

**Response: `200 OK`**

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

### 6.2 Add Comment to Task

`POST /api/v1/tasks/{taskId}/comments`

**Request Body:**

```json
{
  "userId": 1,
  "content": "string"
}
```

**Response: `200 OK`** (CommentResponse)

---

## 7. Task Attachments

### 7.1 List Attachments of Task (Pageable)

`GET /api/v1/tasks/{taskId}/attachments?page=0&size=10`

**Response: `200 OK`**

```json
{
  "content": [
    {
      "id": 1,
      "taskId": 1,
      "uploadedById": 2,
      "uploadedByName": "string",
      "fileName": "report.pdf",
      "fileUrl": "string",
      "fileSize": 1024,
      "uploadedAt": "2026-04-23T10:00:00"
    }
  ],
  "number": 0,
  "size": 10,
  "totalElements": 1,
  "totalPages": 1
}
```

### 7.2 Upload Attachment

`POST /api/v1/tasks/{taskId}/attachments`

**Content-Type:** `multipart/form-data`

**Form Data:**

- `file` (required) - File to upload

**Headers:**

- `X-User` (optional) - Username of uploader

**Response: `200 OK`** (TaskAttachmentResponse)

### 7.3 Delete Attachment

`DELETE /api/v1/attachments/{attachmentId}`

**Response: `204 No Content`**

---

## 8. Notifications

### 8.1 Get Unread Notifications (Pageable)

`GET /api/v1/notifications?userId={userId}&page=0&size=10`

**Query Parameters:**

- `userId` (required) - User ID

**Response: `200 OK`**

```json
{
  "content": [
    {
      "id": 1,
      "userId": 1,
      "type": "TASK_ASSIGNED",
      "message": "string",
      "isRead": false,
      "createdAt": "2026-04-23T10:00:00"
    }
  ],
  "number": 0,
  "size": 10,
  "totalElements": 5,
  "totalPages": 1
}
```

### 8.2 Mark Notification as Read

`PUT /api/v1/notifications/{id}/read`

**Response: `204 No Content`**

---

## 9. Reports

### 9.1 Project Progress Report (Pageable)

`GET /api/v1/reports/project-progress?page=0&size=10`

**Response: `200 OK`**

```json
{
  "content": [
    {
      "projectId": 1,
      "projectName": "string",
      "totalTasks": 20,
      "completedTasks": 12,
      "inProgressTasks": 5,
      "pendingTasks": 3,
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

### 9.2 Employee Performance Report (Pageable)

`GET /api/v1/reports/employee-performance?start={startDate}&end={endDate}&page=0&size=10`

**Query Parameters:**

- `start` (optional) - Start date (ISO format: `2026-01-01T00:00:00`)
- `end` (optional) - End date (ISO format: `2026-12-31T23:59:59`)

**Response: `200 OK`**

```json
{
  "content": [
    {
      "userId": 1,
      "fullName": "string",
      "email": "string",
      "totalTasksAssigned": 15,
      "totalTasksCompleted": 12,
      "totalTasksOverdue": 1,
      "averageCompletionTime": 5.5,
      "performanceScore": 85.0
    }
  ],
  "number": 0,
  "size": 10,
  "totalElements": 1,
  "totalPages": 1
}
```

### 9.3 Export Report

`GET /api/v1/reports/export?type={type}&page=0&size=100`

**Query Parameters:**

- `type` (optional, default: `excel`) - Export format: `excel` or `pdf`

**Response: `200 OK`**

- Content-Type: `application/vnd.openxmlformats-officedocument.spreadsheetml.sheet` (Excel)
- Content-Type: `application/pdf` (PDF)
- Content-Disposition: `attachment; filename=project-progress.xlsx` or `.pdf`

---

## 10. Calendar

### 10.1 Get Tasks for Calendar (Pageable)

`GET /api/v1/calendar/tasks?start={startDate}&end={endDate}&page=0&size=50`

**Query Parameters:**

- `start` (required) - Start date (format: `2026-04-01`)
- `end` (required) - End date (format: `2026-04-30`)

**Response: `200 OK`**

```json
{
  "content": [
    {
      "id": 1,
      "title": "string",
      "description": "string",
      "status": "IN_PROGRESS",
      "priority": "HIGH",
      "dueDate": "2026-04-15T23:59:59",
      "assignedToId": 1,
      "assignedToName": "string"
    }
  ],
  "number": 0,
  "size": 50,
  "totalElements": 1,
  "totalPages": 1
}
```

---

## 11. Audit Logs

### 11.1 Get Audit Logs (Pageable)

`GET /api/v1/audit-logs?page=0&size=20`

**Response: `200 OK`**

```json
{
  "content": [
    {
      "id": 1,
      "action": "CREATE",
      "entityType": "TASK",
      "entityId": 1,
      "userId": 1,
      "userName": "string",
      "timestamp": "2026-04-23T10:00:00",
      "details": "string"
    }
  ],
  "number": 0,
  "size": 20,
  "totalElements": 100,
  "totalPages": 5
}
```

---

## Common HTTP Status Codes

| Code                        | Description                              |
| --------------------------- | ---------------------------------------- |
| `200 OK`                    | Request successful                       |
| `201 Created`               | Resource created successfully            |
| `204 No Content`            | Request successful, no content to return |
| `400 Bad Request`           | Invalid request data                     |
| `401 Unauthorized`          | Authentication required or failed        |
| `403 Forbidden`             | Insufficient permissions                 |
| `404 Not Found`             | Resource not found                       |
| `500 Internal Server Error` | Server error                             |

---

## Error Response Format

```json
{
  "timestamp": "2026-04-23T10:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/v1/tasks"
}
```

---

## Enums Reference

### TaskStatus

- `PENDING` - Task created, waiting to be received
- `IN_PROGRESS` - Task is being worked on
- `COMPLETED` - Task finished, waiting for approval
- `DONE` - Task approved by manager
- `REJECTED` - Task rejected by manager

### Priority

- `LOW`
- `MEDIUM`
- `HIGH`
- `URGENT`

### Role

- `STAFF` - Regular employee
- `MANAGER` - Manager with approval rights

### NotificationType

- `TASK_ASSIGNED`
- `TASK_UPDATED`
- `TASK_COMPLETED`
- `TASK_APPROVED`
- `TASK_REJECTED`
- `COMMENT_ADDED`

### RoleInProject

- `MEMBER` - Project member
- `LEAD` - Project lead

---

## WebSocket

**WebSocket Endpoint:** `ws://localhost:8080/ws`

Used for real-time notifications and chat functionality.

**Configuration:** See `WebSocketConfig.java` for implementation details.

---

## Notes

- All timestamps are in ISO 8601 format
- All date fields accept ISO date format (e.g., `2026-04-23`)
- File uploads support common formats (PDF, DOC, DOCX, XLS, XLSX, images)
- Maximum file upload size: Check application configuration
- JWT tokens expire after configured time (default: 1 hour for access token)
- Refresh tokens have longer expiration (default: 7 days)

---

**For more details, visit Swagger UI at:** `http://localhost:8080/swagger-ui.html`
