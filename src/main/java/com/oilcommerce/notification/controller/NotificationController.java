package com.oilcommerce.notification.controller;
import com.oilcommerce.common.ApiResponse; import com.oilcommerce.notification.dto.NotificationDto;
import com.oilcommerce.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement; import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity; import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails; import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.Map; import java.util.UUID;

@Tag(name="Notifications") @RestController @RequestMapping("/notifications")
@RequiredArgsConstructor @SecurityRequirement(name = "Bearer Authentication")
public class NotificationController {
    private final NotificationService notificationService;
    private UUID uid(UserDetails ud) { return UUID.fromString(ud.getUsername()); }

    @GetMapping public ResponseEntity<ApiResponse<List<NotificationDto>>> get(@AuthenticationPrincipal UserDetails ud,
            @RequestParam(defaultValue="1") int page, @RequestParam(defaultValue="20") int pageSize) {
        return ResponseEntity.ok(ApiResponse.success(notificationService.getNotifications(uid(ud),page,pageSize)));
    }
    @GetMapping("/unread-count") public ResponseEntity<ApiResponse<Map<String,Long>>> unreadCount(@AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(ApiResponse.success(notificationService.getUnreadCount(uid(ud))));
    }
    @PutMapping("/{id}/read") public ResponseEntity<ApiResponse<NotificationDto>> markRead(@AuthenticationPrincipal UserDetails ud, @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success("Marked as read",notificationService.markRead(uid(ud),id)));
    }
    @PutMapping("/read-all") public ResponseEntity<ApiResponse<Void>> markAllRead(@AuthenticationPrincipal UserDetails ud) {
        notificationService.markAllRead(uid(ud)); return ResponseEntity.ok(ApiResponse.success("All marked as read",null));
    }
}
