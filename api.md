# Danh sách API quản lý công việc nội bộ

Dựa trên tài liệu Phân tích Thiết kế Hệ thống Thông tin, dưới đây là danh sách các API (endpoints) cần thiết, được phân loại theo từng nhóm chức năng chính.

## 1. Nhóm API Quản trị & Xác thực (Auth & Admin)

| **Chức năng**          | **API (Endpoint) dự kiến**               | **Mục đích (Tác nhân)**                   |
| ---------------------- | ---------------------------------------- | ----------------------------------------- |
| **Xác thực**           | `POST /api/auth/login`                   | Đăng nhập, trả về JWT Token.              |
| **Xác thực**           | `POST /api/auth/refresh-token`           | Làm mới JWT khi hết hạn.                  |
| **Xác thực**           | `POST /api/auth/logout`                  | Đăng xuất người dùng.                     |
| **Quản lý người dùng** | `GET /api/users`                         | Lấy danh sách thành viên.                 |
| **Quản lý người dùng** | `POST /api/users`                        | Thêm thành viên mới (Manager).            |
| **Quản lý người dùng** | `PUT /api/users/{userId}`                | Chỉnh sửa thông tin thành viên (Manager). |
| **Quản lý người dùng** | `DELETE /api/users/{userId}`             | Xóa thành viên (Manager).                 |
| **Quản lý cơ cấu**     | `CRUD /api/departments`                  | Thêm, Sửa, Xóa phòng ban (Manager).       |
| **Quản lý cơ cấu**     | `CRUD /api/projects`                     | Thêm, Sửa, Xóa dự án (Manager).           |
| **Quản lý cơ cấu**     | `POST /api/projects/{projectId}/members` | Thêm/Xóa thành viên vào dự án.            |

## 2. Nhóm API Điều phối & Quản lý Công việc (Task Orchestration)

| **Chức năng**       | **API (Endpoint) dự kiến**               | **Mục đích (Tác nhân)**                                              |
| ------------------- | ---------------------------------------- | -------------------------------------------------------------------- |
| **Quản lý Task**    | `POST /api/tasks`                        | Tạo công việc mới (Manager).                                         |
| **Quản lý Task**    | `GET /api/tasks`                         | Lấy danh sách công việc (có thể lọc theo User/Project/Status).       |
| **Quản lý Task**    | `GET /api/tasks/{taskId}`                | Xem chi tiết công việc.                                              |
| **Quản lý Task**    | `PUT /api/tasks/{taskId}`                | Cập nhật thông tin công việc (bao gồm thời gian, độ ưu tiên).        |
| **Tiến độ cá nhân** | `PUT /api/tasks/{taskId}/status`         | Cập nhật trạng thái công việc (IN_PROGRESS, Hoàn thành,...) (Staff). |
| **Tiến độ cá nhân** | `POST /api/tasks/{taskId}/receive`       | Ghi nhận nhân viên đã nhận việc (Staff).                             |
| **Tệp đính kèm**    | `POST /api/tasks/{taskId}/attachments`   | Upload tài liệu đính kèm (ảnh, doc, pdf) làm bằng chứng báo cáo.     |
| **Tệp đính kèm**    | `DELETE /api/attachments/{attachmentId}` | Xóa tệp đính kèm.                                                    |

## 3. Nhóm API Phê duyệt (Approval)

| **Chức năng** | **API (Endpoint) dự kiến**         | **Mục đích (Tác nhân)**                                                  |
| ------------- | ---------------------------------- | ------------------------------------------------------------------------ |
| **Phê duyệt** | `POST /api/tasks/{taskId}/approve` | Duyệt công việc, chuyển trạng thái sang DONE (Manager).                  |
| **Từ chối**   | `POST /api/tasks/{taskId}/reject`  | Bác bỏ công việc (kèm lý do), chuyển trạng thái sang REJECTED (Manager). |

## 4. Nhóm API Cộng tác & Giám sát

| **Chức năng**        | **API (Endpoint) dự kiến**                     | **Mục đích (Tác nhân)**                                         |
| -------------------- | ---------------------------------------------- | --------------------------------------------------------------- |
| **Bình luận (Chat)** | `GET /api/tasks/{taskId}/comments`             | Lấy danh sách bình luận trong Task.                             |
| **Bình luận (Chat)** | `POST /api/tasks/{taskId}/comments`            | Gửi bình luận mới.                                              |
| **Lịch biểu**        | `GET /api/calendar/tasks`                      | Lấy danh sách công việc theo phạm vi thời gian (Calendar view). |
| **Thông báo**        | `GET /api/notifications`                       | Lấy danh sách thông báo chưa đọc của người dùng.                |
| **Thông báo**        | `PUT /api/notifications/{notificationId}/read` | Đánh dấu thông báo đã đọc.                                      |
| **Thông báo**        | WebSocket Connection                           | Duy trì kết nối Real-time để nhận thông báo đẩy và chat.        |
| **Báo cáo/Thống kê** | `GET /api/reports/project-progress`            | Lấy dữ liệu tiến độ dự án (Manager).                            |
| **Báo cáo/Thống kê** | `GET /api/reports/employee-performance`        | Lấy dữ liệu hiệu suất nhân viên (Manager).                      |
| **Báo cáo/Thống kê** | `GET /api/reports/export`                      | Xuất báo cáo ra file (Excel/PDF).                               |
| **Audit Log**        | `GET /api/audit-logs`                          | Lấy lịch sử truy vết thay đổi dữ liệu hệ thống (Audit Logging). |
