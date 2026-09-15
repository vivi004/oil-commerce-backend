package com.oilcommerce.brand.dto;
import jakarta.validation.constraints.NotBlank; import lombok.Data;
@Data
public class BrandRequest {
    @NotBlank private String name; private String description;
    private String logo; private String tagline; private String origin; private boolean active = true;
}
