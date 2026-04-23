package learning.tasknode.mapper;

import learning.tasknode.dto.response.NotificationResponse;
import learning.tasknode.entity.Notification;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    NotificationResponse toResponse(Notification entity);
}
