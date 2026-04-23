# Hướng dẫn sử dụng Notification WebSocket (Spring Boot)

## 1. Endpoint WebSocket

- Kết nối WebSocket: `/ws` (qua SockJS)
- Topic notification cá nhân: `/topic/notifications/{userId}`

## 2. Tích hợp frontend (JS/SockJS + Stomp.js)
```javascript
import SockJS from 'sockjs-client';
import Stomp from 'stompjs';

const socket = new SockJS('/ws');
const stompClient = Stomp.over(socket);
stompClient.connect({}, function(frame) {
  // Lặp với chính YOUR_USER_ID
  stompClient.subscribe('/topic/notifications/YOUR_USER_ID', function(message) {
      alert('Notify: ' + message.body); // hoặc xử lý realtime trực tiếp
  });
});
```
- Để nhận realtime notify khi có bình luận task,
  chỉ cần subscribe chính xác topic của user.

## 3. Mô tả tổng quan demo nghiệp vụ
- Khi user bất kỳ bình luận task, backend lấy danh sách thành viên dự án liên quan
  và push notify tới từng người (trừ người gửi), thông qua `/topic/notifications/{userId}`
- Message hiển thị dạng: "<Tên người gửi> vừa bình luận vào công việc: <Tên task>"

## 4. Test nhanh bằng Postman WebSocket (developer)
- Kết nối tới: `ws://localhost:8080/ws/websocket`
- Gửi subscribe frame kiểu STOMP tới channel `/topic/notifications/{userId}`

## 5. Backend không cần broker phức tạp, backend Spring tự quản lý SimpleBroker

Mọi yêu cầu nâng cấp, thay đổi nghiệp vụ hoặc cấu trúc topic vui lòng xem lại code trong NotificationWebSocketService và CommentService.