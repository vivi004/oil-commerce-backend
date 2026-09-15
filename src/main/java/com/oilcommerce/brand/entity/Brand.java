package com.oilcommerce.brand.entity;

import com.oilcommerce.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "brands")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Brand extends BaseEntity {
    @Column(nullable = false, unique = true) private String name;
    @Column(nullable = false, unique = true) private String slug;
    @Column(columnDefinition = "TEXT") private String description;
    @Column private String logo;
    @Column @Builder.Default private boolean active = true;
    @Column private String tagline;
    @Column private String origin;
}
