package com.oilcommerce.product.service;

import com.oilcommerce.brand.entity.Brand;
import com.oilcommerce.brand.repository.BrandRepository;
import com.oilcommerce.category.entity.Category;
import com.oilcommerce.category.repository.CategoryRepository;
import com.oilcommerce.common.PaginatedResponse;
import com.oilcommerce.exception.BusinessException;
import com.oilcommerce.exception.ResourceNotFoundException;
import com.oilcommerce.product.dto.*;
import com.oilcommerce.product.entity.*;
import com.oilcommerce.product.mapper.ProductMapper;
import com.oilcommerce.product.repository.ProductRepository;
import com.oilcommerce.product.repository.ProductVariantRepository;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service @RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ProductMapper productMapper;

    public PaginatedResponse<ProductDto> getProducts(ProductFilterRequest filter) {
        Specification<Product> spec = buildSpec(filter);
        Sort sort = buildSort(filter.getSortBy());
        Pageable pageable = PageRequest.of(filter.getPage() - 1, filter.getPageSize(), sort);
        Page<Product> page = productRepository.findAll(spec, pageable);
        return PaginatedResponse.of(productMapper.toDtoList(page.getContent()), page.getTotalElements(), filter.getPage(), filter.getPageSize());
    }

    public ProductDto getProductByIdOrSlug(String idOrSlug) {
        Product product;
        try {
            product = productRepository.findById(UUID.fromString(idOrSlug))
                    .orElseThrow(() -> new ResourceNotFoundException("Product","id",idOrSlug));
        } catch (IllegalArgumentException e) {
            product = productRepository.findBySlugAndDeletedFalse(idOrSlug)
                    .orElseThrow(() -> new ResourceNotFoundException("Product","slug",idOrSlug));
        }
        return productMapper.toDto(product);
    }

    public List<ProductDto> getFeaturedProducts() {
        Pageable pageable = PageRequest.of(0, 12);
        return productMapper.toDtoList(
            productRepository.findByFeaturedAndStatusAndDeletedFalseOrderByCreatedAtDesc(true, ProductStatus.ACTIVE, pageable));
    }

    public PaginatedResponse<ProductDto> searchProducts(String query, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Page<Product> result = productRepository.searchProducts(query, pageable);
        return PaginatedResponse.of(productMapper.toDtoList(result.getContent()), result.getTotalElements(), page, pageSize);
    }

    public List<ProductDto> getRelatedProducts(UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product","id",productId));
        Pageable pageable = PageRequest.of(0, 6);
        return productMapper.toDtoList(
            productRepository.findRelatedProducts(product.getCategory().getId(), productId, pageable));
    }

    @Transactional
    public ProductDto createProduct(ProductRequest req) {
        // Pre-flight: check for duplicate SKU before hitting the DB constraint
        if (productRepository.existsBySkuAndDeletedFalse(req.getSku())) {
            throw new BusinessException(
                "SKU '" + req.getSku() + "' is already used by another product. Please choose a different Base SKU.",
                org.springframework.http.HttpStatus.CONFLICT);
        }
        Category category = categoryRepository.findById(req.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category","id",req.getCategoryId()));
        Brand brand = req.getBrandId() != null ? brandRepository.findById(req.getBrandId()).orElse(null) : null;

        Product product = Product.builder()
            .name(req.getName()).slug(generateSlug(req.getName()))
            .description(req.getDescription()).shortDescription(req.getShortDescription())
            .price(req.getPrice()).compareAtPrice(req.getCompareAtPrice())
            .sku(req.getSku()).barcode(req.getBarcode())
            .stock(req.getStock()).lowStockThreshold(req.getLowStockThreshold())
            .images(req.getImages()).thumbnail(req.getThumbnail())
            .category(category).brand(brand)
            .tags(req.getTags()).benefits(req.getBenefits())
            .extractionMethod(req.getExtractionMethod()).smokePoint(req.getSmokePoint())
            .purity(req.getPurity()).shelfLife(req.getShelfLife()).origin(req.getOrigin())
            .featured(req.isFeatured()).onSale(req.isOnSale()).bestSeller(req.isBestSeller())
            .status(req.getStatus()).seoTitle(req.getSeoTitle()).seoDescription(req.getSeoDescription())
            .build();

        Product saved = productRepository.save(product);

        if (req.getWeightVariants() != null && !req.getWeightVariants().isEmpty()) {
            List<ProductVariant> variants = req.getWeightVariants().stream().map(v ->
                ProductVariant.builder()
                    .product(saved).code(v.getCode()).label(v.getLabel())
                    .mrp(v.getMrp()).sellingPrice(v.getSellingPrice())
                    .discountPercent(v.getDiscountPercent()).gstPercent(v.getGstPercent())
                    .sku(v.getSku()).barcode(v.getBarcode())
                    .stockQuantity(v.getStockQuantity()).enabled(v.isEnabled()).imageUrl(v.getImageUrl())
                    .build()
            ).toList();
            List<ProductVariant> savedVariants = productVariantRepository.saveAll(variants);
            saved.setVariants(savedVariants);
            productRepository.save(saved);
        }
        return productMapper.toDto(saved);
    }

    @Transactional
    public ProductDto updateProduct(UUID id, ProductRequest req) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product","id",id));
        Category category = categoryRepository.findById(req.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category","id",req.getCategoryId()));
        Brand brand = req.getBrandId() != null ? brandRepository.findById(req.getBrandId()).orElse(null) : null;

        // Pre-flight: check for duplicate SKU on update (exclude current product)
        if (req.getSku() != null && !req.getSku().equals(p.getSku())
                && productRepository.existsBySkuAndDeletedFalseAndIdNot(req.getSku(), id)) {
            throw new BusinessException(
                "SKU '" + req.getSku() + "' is already used by another product. Please choose a different Base SKU.",
                org.springframework.http.HttpStatus.CONFLICT);
        }

        if (req.getName() != null && !req.getName().equalsIgnoreCase(p.getName())) {
            p.setName(req.getName());
            p.setSlug(generateSlug(req.getName()));
        }
        p.setDescription(req.getDescription());
        p.setShortDescription(req.getShortDescription());
        p.setPrice(req.getPrice());
        p.setCompareAtPrice(req.getCompareAtPrice());
        p.setSku(req.getSku());
        p.setBarcode(req.getBarcode());
        p.setStock(req.getStock());
        p.setLowStockThreshold(req.getLowStockThreshold());
        p.setImages(req.getImages());
        p.setThumbnail(req.getThumbnail());
        p.setCategory(category);
        p.setBrand(brand);
        p.setTags(req.getTags());
        p.setBenefits(req.getBenefits());
        p.setExtractionMethod(req.getExtractionMethod());
        p.setSmokePoint(req.getSmokePoint());
        p.setPurity(req.getPurity());
        p.setShelfLife(req.getShelfLife());
        p.setOrigin(req.getOrigin());
        p.setStatus(req.getStatus());
        p.setFeatured(req.isFeatured());
        p.setOnSale(req.isOnSale());
        p.setBestSeller(req.isBestSeller());
        p.setSeoTitle(req.getSeoTitle());
        p.setSeoDescription(req.getSeoDescription());

        if (req.getWeightVariants() != null) {
            List<ProductVariant> existing = productVariantRepository.findByProductIdAndDeletedFalse(p.getId());
            productVariantRepository.deleteAll(existing);

            List<ProductVariant> newVariants = req.getWeightVariants().stream().map(v ->
                ProductVariant.builder()
                    .product(p).code(v.getCode()).label(v.getLabel())
                    .mrp(v.getMrp()).sellingPrice(v.getSellingPrice())
                    .discountPercent(v.getDiscountPercent()).gstPercent(v.getGstPercent())
                    .sku(v.getSku()).barcode(v.getBarcode())
                    .stockQuantity(v.getStockQuantity()).enabled(v.isEnabled()).imageUrl(v.getImageUrl())
                    .build()
            ).toList();
            List<ProductVariant> savedVariants = productVariantRepository.saveAll(newVariants);
            p.setVariants(savedVariants);
        }

        return productMapper.toDto(productRepository.save(p));
    }

    @Transactional
    public void deleteProduct(UUID id) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product","id",id));
        p.setDeleted(true);
        productRepository.save(p);
        List<ProductVariant> variants = productVariantRepository.findByProductIdAndDeletedFalse(p.getId());
        for (ProductVariant v : variants) {
            v.setDeleted(true);
        }
        productVariantRepository.saveAll(variants);
    }

    private Specification<Product> buildSpec(ProductFilterRequest filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.isFalse(root.get("deleted")));
            predicates.add(cb.equal(root.get("status"), ProductStatus.ACTIVE));
            if (filter.getSearch() != null && !filter.getSearch().isBlank()) {
                String like = "%" + filter.getSearch().toLowerCase() + "%";
                predicates.add(cb.or(cb.like(cb.lower(root.get("name")),like),cb.like(cb.lower(root.get("description")),like)));
            }
            if (filter.getCategoryId() != null && !filter.getCategoryId().isBlank()) {
                try { predicates.add(cb.equal(root.get("category").get("id"),UUID.fromString(filter.getCategoryId()))); } catch(Exception ignored){}
            }
            if (filter.getMinPrice() != null) predicates.add(cb.greaterThanOrEqualTo(root.get("price"),filter.getMinPrice()));
            if (filter.getMaxPrice() != null) predicates.add(cb.lessThanOrEqualTo(root.get("price"),filter.getMaxPrice()));
            if (filter.getMinRating() != null) predicates.add(cb.greaterThanOrEqualTo(root.get("rating"),filter.getMinRating()));
            if (Boolean.TRUE.equals(filter.getInStock())) predicates.add(cb.greaterThan(root.get("stock"),0));
            if (Boolean.TRUE.equals(filter.getOnSale())) predicates.add(cb.isTrue(root.get("onSale")));
            if (filter.getBrand() != null && !filter.getBrand().isEmpty()) {
                predicates.add(root.get("brand").get("name").in(filter.getBrand()));
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }

    private Sort buildSort(String sortBy) {
        return switch (sortBy) {
            case "price_asc" -> Sort.by(Sort.Direction.ASC,"price");
            case "price_desc" -> Sort.by(Sort.Direction.DESC,"price");
            case "rating" -> Sort.by(Sort.Direction.DESC,"rating");
            case "popularity" -> Sort.by(Sort.Direction.DESC,"reviewCount");
            default -> Sort.by(Sort.Direction.DESC,"createdAt");
        };
    }

    private String generateSlug(String name) {
        return name.toLowerCase().replaceAll("[^a-z0-9]+","-").replaceAll("^-|-$","");
    }
}
