package com.oilcommerce.category.dto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.UUID;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CategoryRequest {
    @NotBlank private String name;
    private String slug;
    private String description;
    private String image;
    private String icon;
    private UUID parentId;
    private boolean active = true;
    private int sortOrder = 0;
}
