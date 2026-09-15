package com.oilcommerce.brand.mapper;
import com.oilcommerce.brand.dto.BrandDto;
import com.oilcommerce.brand.entity.Brand;
import org.mapstruct.Mapper; import org.mapstruct.Mapping; import java.util.List;
@Mapper(componentModel = "spring")
public interface BrandMapper {
    @Mapping(target = "isActive", source = "active") BrandDto toDto(Brand brand);
    List<BrandDto> toDtoList(List<Brand> brands);
}
