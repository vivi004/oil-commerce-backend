package com.oilcommerce.notification.dto;
import com.oilcommerce.notification.entity.NotificationType;
import lombok.Builder; import lombok.Data; import java.time.Instant; import java.util.UUID;
@Data @Builder
public class NotificationDto {
    private UUID id; private String title; private String message;
    private NotificationType type; private boolean read; private Instant readAt;
    private String actionUrl; private Instant createdAt;
}
