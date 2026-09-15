package com.oilcommerce.brand.repository;
import com.oilcommerce.brand.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List; import java.util.Optional; import java.util.UUID;
@Repository
public interface BrandRepository extends JpaRepository<Brand, UUID> {
    List<Brand> findByActiveAndDeletedFalseOrderByNameAsc(boolean active);
    Optional<Brand> findBySlug(String slug);
    boolean existsByName(String name);
}
