package com.oilcommerce.product.mapper;

import com.oilcommerce.product.dto.*;
import com.oilcommerce.product.entity.*;
import org.mapstruct.*;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "categoryId", expression = "java(p.getCategory() != null ? p.getCategory().getId().toString() : null)")
    @Mapping(target = "categoryName", expression = "java(p.getCategory() != null ? p.getCategory().getName() : null)")
    @Mapping(target = "categorySlug", expression = "java(p.getCategory() != null ? p.getCategory().getSlug() : null)")
    @Mapping(target = "brandId", expression = "java(p.getBrand() != null ? p.getBrand().getId().toString() : null)")
    @Mapping(target = "brand", expression = "java(p.getBrand() != null ? p.getBrand().getName() : null)")
    @Mapping(target = "isFeatured", source = "featured")
    @Mapping(target = "isOnSale", source = "onSale")
    @Mapping(target = "isBestSeller", source = "bestSeller")
    @Mapping(target = "weightVariants", source = "variants")
    @Mapping(target = "discount", ignore = true)
    ProductDto toDto(Product p);

    List<ProductDto> toDtoList(List<Product> products);

    ProductVariantDto variantToDto(ProductVariant v);
}
