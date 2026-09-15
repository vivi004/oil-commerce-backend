package com.oilcommerce.product.repository;

import com.oilcommerce.product.entity.Product;
import com.oilcommerce.product.entity.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {
    Optional<Product> findBySlugAndDeletedFalse(String slug);
    List<Product> findByFeaturedAndStatusAndDeletedFalseOrderByCreatedAtDesc(boolean featured, ProductStatus status, Pageable pageable);
    long countByCategoryIdAndDeletedFalse(UUID categoryId);

    @Query("SELECT p FROM Product p WHERE p.deleted = false AND p.status = 'ACTIVE' " +
           "AND (LOWER(p.name) LIKE LOWER(CONCAT('%',:q,'%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%',:q,'%')))")
    Page<Product> searchProducts(@Param("q") String query, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.deleted = false AND p.status = 'ACTIVE' " +
           "AND p.category.id = :categoryId AND p.id != :excludeId ORDER BY p.rating DESC")
    List<Product> findRelatedProducts(@Param("categoryId") UUID categoryId, @Param("excludeId") UUID excludeId, Pageable pageable);

    List<Product> findByStatusAndDeletedFalseAndStockLessThanEqual(ProductStatus status, int threshold);
}
