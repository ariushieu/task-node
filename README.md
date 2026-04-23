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

### 1.3 Logout

`POST /api/v1/auth/logout`

> Requires: Bearer Token

**Response: `204 No Content`**

---

## 2. Users

### 2.1 Create User

`POST /api/v1/users`

**Request:**
```json
{
  "username": "string (required, 3-50 chars)",
  "email": "string (required, valid email)",
  "password": "string (required, min 6 chars)",
  "fullName": "string (required, max 100)",
  "phone": "string (optional, max 20)",
  "role": "ADMIN | MANAGER | STAFF (required)",
  "departmentId": "number (optional)"
}
```

**Response: `200 OK`**
```json
{
  "id": 1,
  "username": "string",
  "email": "string",
  "fullName": "string",
  "phone": "string",
  "avatarUrl": "string",
  "role": "STAFF",
  "departmentId": 1,
  "departmentName": "string",
  "isActive": true,
  "createdAt": "2026-04-23T10:00:00",
  "updatedAt": "2026-04-23T10:00:00"
}
```

### 2.2 Get All Users (Pageable)

`GET /api/v1/users?page=0&size=10&sort=createdAt,desc`

**Response: `200 OK`** — `Page<UserResponse>`

### 2.3 Update User

`PUT /api/v1/users/{userId}`

**Request:**
```json
{
  "fullName": "string (optional)",
  "phone": "string (optional)",
  "avatarUrl": "string (optional)"
}
```

**Response: `200 OK`** — `UserResponse`

### 2.4 Delete User (Soft Delete)

`DELETE /api/v1/users/{userId}`

**Response: `204 No Content`**

---

## 3. Departments

### 3.1 Create Department

`POST /api/v1/departments`

**Request:**
```json
{
  "name": "string (required, max 100)",
  "description": "string (optional)",
  "managerId": "number (optional)"
}
```

**Response: `200 OK`**
```json
{
  "id": 1,
  "name": "string",
  "description": "string",
  "managerId": 1,
  "managerName": "string",
  "memberCount": 5,
  "createdAt": "2026-04-23T10:00:00",
  "updatedAt": "2026-04-23T10:00:00"
}
```

### 3.2 Get All Departments (Pageable)

`GET /api/v1/departments?page=0&size=10`

**Response: `200 OK`** — `Page<DepartmentResponse>`

---

## 4. Projects

### 4.1 Create Project

`POST /api/v1/projects`

**Request:**
```json
{
  "name": "string (required, max 150)",
  "description": "string (optional)",
  "status": "PLANNING | IN_PROGRESS | ON_HOLD | COMPLETED | CANCELLED (required)",
  "ownerId": "number (required)",
  "startDate": "2026-04-23",
  "endDate": "2026-05-23"
}
```

**Response: `200 OK`**
```json
{
  "id": 1,
  "name": "string",
  "description": "string",
  "status": "PLANNING",
  "ownerId": 1,
  "ownerName": "string",
  "startDate": "2026-04-23",
  "endDate": "2026-05-23",
  "memberCount": 3,
  "taskCount": 10,
  "members": [],
  "createdAt": "2026-04-23T10:00:00",
  "updatedAt": "2026-04-23T10:00:00"
}
```

### 4.2 Get All Projects (Pageable)

`GET /api/v1/projects?page=0&size=10`

**Response: `200 OK`** — `Page<ProjectResponse>`

### 4.3 Get Project by ID

`GET /api/v1/projects/{id}`

**Response: `200 OK`** — `ProjectResponse`

### 4.4 Update Project

`PUT /api/v1/projects/{id}`

**Request:**
```json
{
  "name": "string (optional)",
  "description": "string (optional)",
  "status": "IN_PROGRESS (optional)",
  "startDate": "2026-04-23 (optional)",
  "endDate": "2026-05-23 (optional)"
}
```

**Response: `200 OK`** — `ProjectResponse`

### 4.5 Delete Project (Soft Delete)

`DELETE /api/v1/projects/{id}`

**Response: `204 No Content`**

### 4.6 Add Member to Project

`POST /api/v1/projects/{projectId}/members`

**Request:**
```json
{
  "userId": "number (required)",
  "roleInProject": "OWNER | MANAGER | MEMBER | VIEWER (required)"
}
```

**Response: `200 OK`**
```json
{
  "id": 1,
  "projectId": 1,
  "userId": 2,
  "userName": "string",
  "roleInProject": "MEMBER",
  "createdAt": "2026-04-23T10:00:00"
}
```

### 4.7 Remove Member from Project

`DELETE /api/v1/projects/{projectId}/members/{userId}`

**Response: `204 No Content`**

### 4.8 List Project Members

`GET /api/v1/projects/{projectId}/members`

**Response: `200 OK`** — `List<ProjectMemberResponse>`

---

## 5. Tasks

### Task Workflow

```
NEW → TODO → IN_PROGRESS → IN_REVIEW → WAITING_APPROVAL
                                             ↓
                                     APPROVED / REJECTED
```

### 5.1 Create Task

`POST /api/v1/tasks`

> `createdBy` is automatically set from the authenticated user.  
> If `status` is not provided, defaults to `NEW`.

**Request:**
```json
{
  "title": "string (required, max 200)",
  "description": "string (optional)",
  "status": "NEW | TODO | IN_PROGRESS | IN_REVIEW | WAITING_APPROVAL (required)",
  "priority": "LOW | MEDIUM | HIGH | CRITICAL (required)",
  "startDate": "2026-04-23 (optional)",
  "endDate": "2026-05-23 (optional)",
  "projectId": "number (required)",
  "assigneeId": "number (optional)",
  "parentTaskId": "number (optional)",
  "tagIds": [1, 2, 3]
}
```

**Response: `200 OK`**
```json
{
  "id": 1,
  "title": "string",
  "description": "string",
  "status": "NEW",
  "priority": "MEDIUM",
  "startDate": "2026-04-23",
  "endDate": "2026-05-23",
  "projectId": 1,
  "projectName": "string",
  "assigneeId": 2,
  "assigneeName": "string",
  "assigneeAvatarUrl": "string",
  "createdById": 1,
  "createdByName": "string",
  "parentTaskId": null,
  "parentTaskTitle": null,
  "tags": [],
  "attachments": [],
  "commentCount": 0,
  "subTaskCount": 0,
  "createdAt": "2026-04-23T10:00:00",
  "updatedAt": "2026-04-23T10:00:00"
}
```

### 5.2 Get All Tasks (Pageable)

`GET /api/v1/tasks?page=0&size=10&sort=createdAt,desc`

**Response: `200 OK`** — `Page<TaskResponse>`

### 5.3 Get Task by ID

`GET /api/v1/tasks/{id}`

**Response: `200 OK`** — `TaskResponse`

### 5.4 Update Task

`PUT /api/v1/tasks/{id}`

**Request:**
```json
{
  "title": "string (optional)",
  "description": "string (optional)",
  "status": "IN_PROGRESS (optional)",
  "priority": "HIGH (optional)",
  "startDate": "2026-04-23 (optional)",
  "endDate": "2026-05-23 (optional)",
  "assigneeId": "number (optional)",
  "parentTaskId": "number (optional)",
  "tagIds": [1, 2]
}
```

**Response: `200 OK`** — `TaskResponse`

### 5.5 Delete Task (Soft Delete)

`DELETE /api/v1/tasks/{id}`

**Response: `204 No Content`**

### 5.6 Update Task Status

`PUT /api/v1/tasks/{id}/status`

**Request:**
```json
{
  "status": "IN_PROGRESS | IN_REVIEW | WAITING_APPROVAL | DONE (required)"
}
```

**Response: `200 OK`** — `TaskResponse`

### 5.7 Receive Task

`POST /api/v1/tasks/{id}/receive`

> Assigns the task to the user. If status is `NEW` or `TODO`, automatically changes to `IN_PROGRESS`.

**Request:**
```json
{
  "userId": "number (required)"
}
```

**Response: `200 OK`** — `TaskResponse`

### 5.8 Approve Task

`POST /api/v1/tasks/{id}/approve`

> Task **must** be in `WAITING_APPROVAL` status. Sets status to `APPROVED` and records `completedAt`.

**Response: `200 OK`** — `TaskResponse`

### 5.9 Reject Task

`POST /api/v1/tasks/{id}/reject`

> Task **must** be in `WAITING_APPROVAL` status. Sets status to `REJECTED` and appends rejection reason to description.

**Request:**
```json
{
  "reason": "string (required)"
}
```

**Response: `200 OK`** — `TaskResponse`

---

## 6. Comments

### 6.1 Get Comments of Task

`GET /api/v1/tasks/{taskId}/comments?userId={currentUserId}`

> User must be a member of the task's project.

**Response: `200 OK`**
```json
[
  {
    "id": 1,
    "userId": 2,
    "userName": "string",
    "userAvatarUrl": "string",
    "content": "string",
    "createdAt": "2026-04-23T10:00:00"
  }
]
```

### 6.2 Add Comment

`POST /api/v1/tasks/{taskId}/comments`

> User must be a member of the task's project. Realtime WebSocket notification is sent to all other project members.

**Request:**
```json
{
  "userId": "number (required)",
  "content": "string (required)"
}
```

**Response: `200 OK`** — `CommentResponse`

---

## 7. Task Attachments

### 7.1 List Attachments of Task

`GET /api/v1/tasks/{taskId}/attachments`

**Response: `200 OK`**
```json
[
  {
    "id": 1,
    "taskId": 1,
    "uploadedById": 2,
    "uploadedByName": "string",
    "fileName": "report.pdf",
    "fileUrl": "uploads/task-1/uuid_report.pdf",
    "fileType": "application/pdf",
    "fileSize": 102400,
    "createdAt": "2026-04-23T10:00:00"
  }
]
```

### 7.2 Upload Attachment

`POST /api/v1/tasks/{taskId}/attachments`

> Content-Type: `multipart/form-data`  
> Accepted formats: `.pdf`, `.doc`, `.docx`  
> Uploader is resolved from `X-User` header or defaults to `system`.

**Request:** Form data with `file` field.

**Response: `200 OK`** — `TaskAttachmentResponse`

### 7.3 Delete Attachment

`DELETE /api/v1/attachments/{attachmentId}`

> Soft deletes the record and removes the physical file.

**Response: `204 No Content`**

---

## 8. Notifications

### 8.1 Get Unread Notifications

`GET /api/v1/notifications?userId={userId}`

**Response: `200 OK`**
```json
[
  {
    "id": 1,
    "title": "string",
    "message": "string",
    "type": "TASK_ASSIGNED | TASK_COMMENT | TASK_STATUS_CHANGED | TASK_APPROVED | TASK_REJECTED | APPROVAL_REQUESTED | PROJECT_UPDATE",
    "referenceId": 1,
    "referenceType": "string",
    "isRead": false,
    "createdAt": "2026-04-23T10:00:00"
  }
]
```

### 8.2 Mark Notification as Read

`PUT /api/v1/notifications/{id}/read`

**Response: `204 No Content`**

### 8.3 WebSocket (Realtime)

> Subscribe: `ws://localhost:8080/ws`  
> Topic: `/topic/notifications/{userId}`

---

## 9. Reports

### 9.1 Project Progress

`GET /api/v1/reports/project-progress`

**Response: `200 OK`**
```json
[
  {
    "projectId": 1,
    "projectName": "string",
    "totalTasks": 20,
    "completedTasks": 12,
    "inProgressTasks": 5,
    "overdueTasks": 2,
    "percentCompleted": 60.0
  }
]
```

### 9.2 Employee Performance

`GET /api/v1/reports/employee-performance?start=2026-01-01T00:00:00&end=2026-12-31T23:59:59`

> Both `start` and `end` are optional (ISO 8601 `LocalDateTime` format).

**Response: `200 OK`**
```json
[
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
]
```

### 9.3 Export Report

`GET /api/v1/reports/export?type=excel`

> `type`: `excel` (default) or `pdf`  
> Returns binary file download.

**Response: `200 OK`** — Binary file  
- Excel: `Content-Type: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet`  
- PDF: `Content-Type: application/pdf`

---

## 10. Calendar

### 10.1 Get Tasks for Calendar

`GET /api/v1/calendar/tasks?start=2026-04-01&end=2026-04-30&page=0&size=50`

> Returns tasks within the given date range (by `startDate` / `endDate`).

**Response: `200 OK`** — `Page<TaskResponse>`

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
      "entityId": 5,
      "details": "Created task: Fix login bug",
      "userId": 1,
      "userName": "admin",
      "createdAt": "2026-04-23T10:00:00"
    }
  ],
  "totalPages": 5,
  "totalElements": 100
}
```

---

## Error Responses

### 400 Bad Request (Validation Error)
```json
{
  "timestamp": "2026-04-23T10:00:00",
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
  "timestamp": "2026-04-23T10:00:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Bad credentials"
}
```

### 403 Forbidden (Token expired)
```json
{
  "timestamp": "2026-04-23T10:00:00",
  "status": 403,
  "error": "Forbidden",
  "message": "Refresh token was expired. Please make a new login request"
}
```

### 404 Not Found
```json
{
  "timestamp": "2026-04-23T10:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Task not found"
}
```

---

## Enums Reference

| Enum | Values |
|------|--------|
| `UserRole` | `ADMIN`, `MANAGER`, `STAFF` |
| `ProjectStatus` | `PLANNING`, `IN_PROGRESS`, `ON_HOLD`, `COMPLETED`, `CANCELLED` |
| `ProjectRole` | `OWNER`, `MANAGER`, `MEMBER`, `VIEWER` |
| `TaskStatus` | `NEW`, `TODO`, `IN_PROGRESS`, `IN_REVIEW`, `WAITING_APPROVAL`, `APPROVED`, `REJECTED`, `DONE` |
| `TaskPriority` | `LOW`, `MEDIUM`, `HIGH`, `CRITICAL` |
| `NotificationType` | `TASK_ASSIGNED`, `TASK_COMMENT`, `TASK_STATUS_CHANGED`, `TASK_APPROVED`, `TASK_REJECTED`, `APPROVAL_REQUESTED`, `PROJECT_UPDATE` |
| `ApprovalAction` | `APPROVE`, `REJECT` |
