package com.oilcommerce.platformsettings.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "platform_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlatformSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "edible_oil_gst", precision = 5, scale = 2, nullable = false)
    private BigDecimal edibleOilGst;

    @Column(name = "lamp_oil_gst", precision = 5, scale = 2, nullable = false)
    private BigDecimal lampOilGst;

    @Column(name = "mandi_sheet_url", columnDefinition = "TEXT", nullable = false)
    private String mandiSheetUrl;

    @Column(name = "auto_sync_enabled", nullable = false)
    private boolean autoSyncEnabled;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
