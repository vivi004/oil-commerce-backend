package com.oilcommerce.notification.repository;
import com.oilcommerce.notification.entity.Notification;
import org.springframework.data.domain.*; import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;
@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    Page<Notification> findByUserIdAndDeletedFalseOrderByCreatedAtDesc(UUID userId, Pageable pageable);
    long countByUserIdAndReadFalseAndDeletedFalse(UUID userId);
}
