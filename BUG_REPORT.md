# Tổng kết vấn đề khi hoàn thiện API

- Rất nhiều lỗi "cannot resolve symbol", "could not autowire", "method not found", "missing getter",... xảy ra do:
    - Thiếu hoặc sai tên các method trong repository so với service/controller gọi tới.
    - Entity thiếu getter/setter hoặc field cần thiết được gọi từ service (ví dụ: User thiếu getDepartment hay getFullName).
    - Repository không đặt đúng package, sai tên file.
    - DTO thiếu trường hoặc class, sai package.
    - Import không đúng đường dẫn, quên sửa lại sau khi đổi cấu trúc project.
    - Luồng phụ thuộc giữa Service/Repo/Entity chưa đồng bộ.

- Đã thực hiện:
    - Chủ động quét toàn bộ project, rà soát tất cả các file service, repo, entity, dto, mapper.
    - Đề xuất fix triệt để signature, field, method cần thiết ở mọi vị trí gọi tới.
    - Viết lại chuẩn các repository, entity, dto cần thiết để đảm bảo không bị unresolved symbol/autowire.
    - Luôn tự động hóa, không yêu cầu copy/paste thủ công của người dùng.

- **Khuyến nghị:**
    - Nếu còn gặp lỗi tương tự, hãy rà lại đồng bộ khai báo tên hàm, tên class, import, annotation đúng chuẩn.
    - Nên dùng chức năng "Build Project" liên tục và chú ý warning trong IDE để sớm fix tận gốc.

- Quá trình fix các lỗi lớn/tốn thời gian khi project nhiều file chồng lấn phụ thuộc:
    - Chuẩn hóa từng khối Service/Repo/Entity/DTO.
    - Gặp lỗi tên/symbol phải viết lại luôn interface/repo/entity và field.
    - Nhiều lỗi cú pháp/cấu trúc sẽ lan dây chuyền nếu thiếu field, thiếu getter/setter, hoặc thiếu method repo.

> Lưu ý: Các lỗi này xuất hiện khi project chuyển đoạn lớn, thay đổi business logic hoặc tận dụng lại code mà chưa đồng bộ lại các interface/entity/dto.
