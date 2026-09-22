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

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLogo() { return logo; }
    public void setLogo(String logo) { this.logo = logo; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public String getTagline() { return tagline; }
    public void setTagline(String tagline) { this.tagline = tagline; }

    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }
}
