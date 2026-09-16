package com.oilcommerce.platformsettings.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "mandi_benchmarks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MandiBenchmark {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false)
    private String commodity;

    @Column(name = "hsn_code", nullable = false, length = 50)
    private String hsnCode;

    @Column(name = "crop_season", length = 100)
    private String cropSeason;

    @Column(name = "mandi_benchmark_rate", precision = 10, scale = 2, nullable = false)
    private BigDecimal mandiBenchmarkRate;

    @Column(name = "edible_classification", nullable = false, length = 100)
    private String edibleClassification;

    @Column(name = "gst_bracket", precision = 5, scale = 2, nullable = false)
    private BigDecimal gstBracket;

    @Column(name = "effective_date", nullable = false, length = 100)
    private String effectiveDate;

    @Column(nullable = false, length = 50)
    private String status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
