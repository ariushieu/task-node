package learning.tasknode.service;

import learning.tasknode.dto.response.NotificationResponse;
import learning.tasknode.entity.Notification;
import learning.tasknode.exception.ResourceNotFoundException;
import learning.tasknode.mapper.NotificationMapper;
import learning.tasknode.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    public List<NotificationResponse> getUnreadNotifications(Long userId) {
        return notificationRepository.findByRecipientIdAndIsReadFalseOrderByCreatedAtDesc(userId)
                .stream().map(notificationMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        Notification n = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông báo!"));
        n.setIsRead(true);
        notificationRepository.save(n);
    }
}
