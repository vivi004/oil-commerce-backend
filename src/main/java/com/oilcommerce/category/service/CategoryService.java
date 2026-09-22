package com.oilcommerce.category.service;

import com.oilcommerce.category.dto.CategoryDto;
import com.oilcommerce.category.dto.CategoryRequest;
import com.oilcommerce.category.entity.Category;
import com.oilcommerce.category.mapper.CategoryMapper;
import com.oilcommerce.category.repository.CategoryRepository;
import com.oilcommerce.exception.ResourceNotFoundException;
import com.oilcommerce.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final ProductRepository productRepository;

    @Cacheable("categories")
    public List<CategoryDto> getAllCategories() {
        List<Category> categories = categoryRepository.findByActiveAndDeletedFalseOrderBySortOrderAsc(true);
        return categories.stream().map(c -> {
            CategoryDto dto = categoryMapper.toDto(c);
            long count = productRepository.countByCategoryIdAndDeletedFalse(c.getId());
            return CategoryDto.builder()
                    .id(dto.getId()).name(dto.getName()).slug(dto.getSlug())
                    .description(dto.getDescription()).image(dto.getImage()).icon(dto.getIcon())
                    .parentId(dto.getParentId()).isActive(dto.isActive()).sortOrder(dto.getSortOrder())
                    .productCount((int) count).createdAt(dto.getCreatedAt())
                    .build();
        }).toList();
    }

    public CategoryDto getCategoryByIdOrSlug(String idOrSlug) {
        Category category;
        try {
            UUID uuid = UUID.fromString(idOrSlug);
            category = categoryRepository.findByIdAndDeletedFalse(uuid)
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", idOrSlug));
        } catch (IllegalArgumentException e) {
            category = categoryRepository.findBySlug(idOrSlug)
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "slug", idOrSlug));
        }
        return categoryMapper.toDto(category);
    }

    @Transactional
    @CacheEvict(value = "categories", allEntries = true)
    public CategoryDto createCategory(CategoryRequest request) {
        String slug = generateSlug(request.getName());
        Optional<Category> bySlug = categoryRepository.findBySlug(slug);
        if (bySlug.isPresent()) {
            return categoryMapper.toDto(bySlug.get());
        }
        Optional<Category> byName = categoryRepository.findAll().stream()
                .filter(c -> c.getName() != null && c.getName().equalsIgnoreCase(request.getName().trim()))
                .findFirst();
        if (byName.isPresent()) {
            return categoryMapper.toDto(byName.get());
        }

        Category category = Category.builder()
                .name(request.getName().trim())
                .slug(slug)
                .description(request.getDescription())
                .image(request.getImage())
                .icon(request.getIcon() != null ? request.getIcon() : "🛢️")
                .active(request.isActive())
                .sortOrder(request.getSortOrder())
                .build();
        if (request.getParentId() != null) {
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getParentId()));
            category.setParent(parent);
        }
        return categoryMapper.toDto(categoryRepository.save(category));
    }

    @Transactional
    @CacheEvict(value = "categories", allEntries = true)
    public CategoryDto updateCategory(UUID id, CategoryRequest request) {
        Category category = categoryRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setImage(request.getImage());
        category.setIcon(request.getIcon());
        category.setActive(request.isActive());
        category.setSortOrder(request.getSortOrder());
        return categoryMapper.toDto(categoryRepository.save(category));
    }

    @Transactional
    @CacheEvict(value = "categories", allEntries = true)
    public void deleteCategory(UUID id) {
        Category category = categoryRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        category.setDeleted(true);
        categoryRepository.save(category);
    }

    private String generateSlug(String name) {
        return name.toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
    }
}
