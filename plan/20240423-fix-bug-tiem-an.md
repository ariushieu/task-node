# KẾ HOẠCH SỬA LỖI TIỀM ẨN & CHUẨN HÓA SPRING BOOT (2026-04-23)

## 1. Mục tiêu
Đưa codebase về đúng chuẩn Spring Boot thực chiến như quy định ở CLAUDE.md, giải quyết toàn bộ các điểm trừ vừa phát hiện:
- Chuẩn hóa trả về Page<T> + Pageable ở mọi API collection
- Loại bỏ việc lộ entity khỏi controller (chỉ trả về DTO)
- Thay thế IllegalArgumentException bằng custom exception với message rõ nghĩa
- Đảm bảo luôn filter isDeleted = false trong mọi truy vấn entity hỗ trợ soft delete
- Ngăn rò rỉ trường nhạy cảm (password, token...)
- Rà lại validation, null safety cũng như naming convention quan trọng nhất

## 2. Phạm vi & các bước thực hiện

### Bước 1: Sửa các endpoint trả về List<>
- Sửa Controller, Service các API trả về List<> → chuyển sang Page<T>
- Thêm tham số Pageable, refactor service sử dụng Repository trả về Page<...>
- Đảm bảo mọi trường hợp trả về Collection đều hỗ trợ phân trang
- Sửa các file:
  - TaskAttachmentController, ReportController, NotificationController, CommentController, ProjectController, ...
  - Service, repository liên quan

### Bước 2: Loại bỏ lộ entity ở controller
- Không import hay trả về entity trong controller
- Rà soát toàn bộ controller, đổi sang Response DTO
- Kiểm tra import và return type

### Bước 3: Thay thế IllegalArgumentException
- Tìm và thay các nơi throw IllegalArgumentException bằng custom exception (ResourceNotFoundException, BadRequestException, ...)
- Sửa GlobalExceptionHandler nếu cần để bắt các exception mới
- Đảm bảo message trả về rõ ràng, dùng @RestControllerAdvice

### Bước 4: Soft delete triệt để
- Kiểm tra mọi repository/query/entity có hỗ trợ soft delete
- Đảm bảo repository chỉ trả về bản ghi isDeleted = false (JPQL/Query method đều phải đủ điều kiện này)
- Rà lại service không tự động xoá cứng hoặc pha trộn delete cứng/mềm

### Bước 5: Ngăn rò rỉ trường nhạy cảm
- Kiểm soát tất cả các mapper, DTO đảm bảo không trả về field sensitive (password, token, ...)

### Bước 6: Validation & null safety
- Đảm bảo mọi Request DTO đều có annotation validation phù hợp
- Controller luôn có @Valid ở đầu vào
- Service/controller không trả null trực tiếp, dùng Optional hoặc ResponseEntity phù hợp
- Kiểm lại tên class, field, method tuân thủ chuẩn camelCase, PascalCase

### Bước 7: Kiểm thử lại API (manually/postman)
- Test endpoints phổ biến trả về phân trang, check lỗi trả về, catch các bug thường gặp

## 3. Lưu ý
- Một số thay đổi lớn có thể ảnh hưởng tới Frontend (API contract), cần phối hợp kiểm thử lại sau khi hoàn tất.
- Tất cả chỉnh sửa đều tuân thủ CLAUDE.md code-style.

## 4. Phê duyệt & thực hiện
Vui lòng xác nhận lại kế hoạch. Sau khi duyệt xong tôi sẽ tiến hành implement từng bước chi tiết, commit rõ ràng theo từng phần.
