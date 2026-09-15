package com.oilcommerce.notification.entity;
import com.oilcommerce.common.BaseEntity; import com.oilcommerce.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity @Table(name = "notifications")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Notification extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id", nullable = false) private User user;
    @Column(nullable = false) private String title;
    @Column(nullable = false, columnDefinition = "TEXT") private String message;
    @Enumerated(EnumType.STRING) @Column(nullable = false) @Builder.Default private NotificationType type = NotificationType.SYSTEM;
    @Column @Builder.Default private boolean read = false;
    @Column private Instant readAt;
    @Column private String actionUrl;
}
