package com.oilcommerce.brand.service;

import com.oilcommerce.brand.dto.*; import com.oilcommerce.brand.entity.Brand;
import com.oilcommerce.brand.mapper.BrandMapper; import com.oilcommerce.brand.repository.BrandRepository;
import com.oilcommerce.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;

@Service @RequiredArgsConstructor
public class BrandService {
    private final BrandRepository brandRepository; private final BrandMapper brandMapper;

    public List<BrandDto> getAllBrands() {
        return brandMapper.toDtoList(brandRepository.findByActiveAndDeletedFalseOrderByNameAsc(true));
    }

    @Transactional
    public BrandDto createBrand(BrandRequest req) {
        String slug = req.getName().trim().toLowerCase().replaceAll("[^a-z0-9]+","-");
        Optional<Brand> existing = brandRepository.findAll().stream()
            .filter(b -> b.getName() != null && (b.getName().equalsIgnoreCase(req.getName().trim()) || slug.equalsIgnoreCase(b.getSlug())))
            .findFirst();
        if (existing.isPresent()) {
            return brandMapper.toDto(existing.get());
        }

        Brand brand = Brand.builder()
            .name(req.getName().trim()).slug(slug)
            .description(req.getDescription()).logo(req.getLogo())
            .tagline(req.getTagline()).origin(req.getOrigin()).active(req.isActive()).build();
        return brandMapper.toDto(brandRepository.save(brand));
    }

    @Transactional
    public BrandDto updateBrand(UUID id, BrandRequest req) {
        Brand b = brandRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Brand","id",id));
        b.setName(req.getName()); b.setDescription(req.getDescription()); b.setLogo(req.getLogo());
        b.setTagline(req.getTagline()); b.setOrigin(req.getOrigin()); b.setActive(req.isActive());
        return brandMapper.toDto(brandRepository.save(b));
    }

    @Transactional
    public void deleteBrand(UUID id) {
        Brand b = brandRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Brand","id",id));
        b.setDeleted(true); brandRepository.save(b);
    }
}
