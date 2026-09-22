package com.oilcommerce.brand.dto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank; import lombok.Data;
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BrandRequest {
    @NotBlank private String name; private String description;
    private String logo; private String tagline; private String origin; private boolean active = true;
}
