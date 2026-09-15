package com.oilcommerce.brand.dto;
import lombok.Builder; import lombok.Data; import java.util.UUID;
@Data @Builder
public class BrandDto {
    private UUID id; private String name; private String slug;
    private String description; private String logo; private String tagline;
    private String origin; private boolean isActive;
}
