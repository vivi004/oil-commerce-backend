package com.oilcommerce.category.dto;
import lombok.Builder;
import lombok.Data;
import java.time.Instant;
import java.util.UUID;

@Data @Builder
public class CategoryDto {
    private UUID id;
    private String name;
    private String slug;
    private String description;
    private String image;
    private String icon;
    private UUID parentId;
    private boolean isActive;
    private int sortOrder;
    private int productCount;
    private Instant createdAt;
}
