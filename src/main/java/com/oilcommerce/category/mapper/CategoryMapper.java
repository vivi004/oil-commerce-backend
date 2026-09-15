package com.oilcommerce.category.mapper;

import com.oilcommerce.category.dto.CategoryDto;
import com.oilcommerce.category.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    @Mapping(target = "parentId", source = "parent.id")
    @Mapping(target = "productCount", ignore = true)
    @Mapping(target = "isActive", source = "active")
    CategoryDto toDto(Category category);
    List<CategoryDto> toDtoList(List<Category> categories);
}
