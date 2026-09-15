package com.oilcommerce.notification.service;

import com.oilcommerce.exception.ResourceNotFoundException;
import com.oilcommerce.notification.dto.NotificationDto; import com.oilcommerce.notification.entity.Notification;
import com.oilcommerce.notification.repository.NotificationRepository;
import com.oilcommerce.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*; import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant; import java.util.List; import java.util.Map; import java.util.UUID;

@Service @RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository; private final UserRepository userRepository;

    public List<NotificationDto> getNotifications(UUID userId, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page-1, pageSize);
        return notificationRepository.findByUserIdAndDeletedFalseOrderByCreatedAtDesc(userId, pageable)
                .map(this::toDto).getContent();
    }

    public Map<String,Long> getUnreadCount(UUID userId) {
        return Map.of("count", notificationRepository.countByUserIdAndReadFalseAndDeletedFalse(userId));
    }

    @Transactional
    public NotificationDto markRead(UUID userId, UUID notifId) {
        Notification n = notificationRepository.findById(notifId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification","id",notifId));
        n.setRead(true); n.setReadAt(Instant.now());
        return toDto(notificationRepository.save(n));
    }

    @Transactional
    public void markAllRead(UUID userId) {
        notificationRepository.findByUserIdAndDeletedFalseOrderByCreatedAtDesc(userId, Pageable.unpaged())
                .forEach(n -> { n.setRead(true); n.setReadAt(Instant.now()); notificationRepository.save(n); });
    }

    public void sendNotification(UUID userId, String title, String message, String type) {
        userRepository.findById(userId).ifPresent(user -> {
            Notification n = Notification.builder().user(user).title(title).message(message)
                    .type(com.oilcommerce.notification.entity.NotificationType.valueOf(type)).build();
            notificationRepository.save(n);
        });
    }

    private NotificationDto toDto(Notification n) {
        return NotificationDto.builder().id(n.getId()).title(n.getTitle()).message(n.getMessage())
            .type(n.getType()).read(n.isRead()).readAt(n.getReadAt())
            .actionUrl(n.getActionUrl()).createdAt(n.getCreatedAt()).build();
    }
}
