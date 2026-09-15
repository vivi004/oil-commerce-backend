package com.oilcommerce.category.repository;

import com.oilcommerce.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    Optional<Category> findBySlug(String slug);
    Optional<Category> findByIdAndDeletedFalse(UUID id);
    List<Category> findByParentIsNullAndDeletedFalseOrderBySortOrderAsc();
    List<Category> findByActiveAndDeletedFalseOrderBySortOrderAsc(boolean active);
    boolean existsByName(String name);
}
