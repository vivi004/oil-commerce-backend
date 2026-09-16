package com.oilcommerce.inventory.service;

import com.oilcommerce.exception.ResourceNotFoundException;
import com.oilcommerce.inventory.dto.StockAdjustmentRequest;
import com.oilcommerce.inventory.dto.StockInventoryItemDto;
import com.oilcommerce.inventory.dto.StockMovementDto;
import com.oilcommerce.inventory.entity.Inventory;
import com.oilcommerce.inventory.entity.StockMovement;
import com.oilcommerce.inventory.repository.InventoryRepository;
import com.oilcommerce.inventory.repository.StockMovementRepository;
import com.oilcommerce.product.entity.Product;
import com.oilcommerce.product.entity.ProductVariant;
import com.oilcommerce.product.repository.ProductRepository;
import com.oilcommerce.product.repository.ProductVariantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final StockMovementRepository stockMovementRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;

    @Transactional(readOnly = true)
    public List<StockInventoryItemDto> getAllStockItems() {
        List<Product> products = productRepository.findAll();
        Map<UUID, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, p -> p, (a, b) -> a));

        List<ProductVariant> variants = productVariantRepository.findByDeletedFalse();
        Map<UUID, Inventory> inventoryMap = inventoryRepository.findByDeletedFalse().stream()
                .filter(inv -> inv.getVariantId() != null)
                .collect(Collectors.toMap(Inventory::getVariantId, inv -> inv, (a, b) -> a));

        List<StockInventoryItemDto> result = new ArrayList<>();

        for (ProductVariant v : variants) {
            UUID prodId = (v.getProduct() != null) ? v.getProduct().getId() : null;
            if (prodId == null) continue;
            Product p = productMap.get(prodId);
            if (p == null) continue;

            Inventory inv = inventoryMap.get(v.getId());
            int stock = v.getStockQuantity();
            int reorderLevel = 20;

            String status = "IN_STOCK";
            if (stock <= 0) {
                status = "OUT_OF_STOCK";
            } else if (stock <= reorderLevel) {
                status = "LOW_STOCK";
            }

            String primaryImage = (p.getImages() != null && !p.getImages().isEmpty())
                    ? p.getImages().get(0)
                    : (p.getThumbnail() != null ? p.getThumbnail() : "");

            String brandName = p.getBrand() != null ? p.getBrand().getName() : "Nisha Pure Oils";
            String categoryName = p.getCategory() != null ? p.getCategory().getName() : "Edible Cold Pressed Oils";
            String whLocation = (inv != null && inv.getWarehouseLocation() != null)
                    ? inv.getWarehouseLocation()
                    : "Unit #1 Mara Chekku Shed";

            result.add(StockInventoryItemDto.builder()
                    .productId(p.getId())
                    .variantId(v.getId())
                    .productName(p.getName())
                    .primaryImage(primaryImage)
                    .brand(brandName)
                    .category(categoryName)
                    .sku(v.getSku())
                    .variantSize(v.getCode() != null ? v.getCode() : v.getLabel())
                    .sellingPrice(v.getSellingPrice() != null ? v.getSellingPrice().doubleValue() : 0.0)
                    .mrp(v.getMrp() != null ? v.getMrp().doubleValue() : 0.0)
                    .stockQuantity(stock)
                    .reorderLevel(reorderLevel)
                    .batchNumber("MC-" + p.getId().toString().substring(0, 4).toUpperCase() + "-B" + (v.getCode() != null ? v.getCode().replaceAll("\\D", "") : "1"))
                    .warehouseLocation(whLocation)
                    .status(status)
                    .build());
        }

        return result;
    }

    @Transactional
    public StockInventoryItemDto adjustStock(StockAdjustmentRequest request) {
        ProductVariant variant;
        if (request.getVariantId() != null) {
            variant = productVariantRepository.findById(request.getVariantId())
                    .orElseThrow(() -> new ResourceNotFoundException("ProductVariant", "id", request.getVariantId()));
        } else if (request.getSku() != null) {
            variant = productVariantRepository.findBySkuAndDeletedFalse(request.getSku())
                    .orElseThrow(() -> new ResourceNotFoundException("ProductVariant", "sku", request.getSku()));
        } else {
            throw new IllegalArgumentException("Either variantId or sku must be provided");
        }

        Product product = variant.getProduct();
        if (product == null && request.getProductId() != null) {
            product = productRepository.findById(request.getProductId()).orElse(null);
        }

        int previousStock = variant.getStockQuantity();
        int newStock = Math.max(0, previousStock + request.getQuantityChange());
        variant.setStockQuantity(newStock);
        productVariantRepository.save(variant);

        // Update or create inventory row
        Inventory inventory = inventoryRepository.findByVariantIdAndDeletedFalse(variant.getId())
                .orElse(null);

        if (inventory == null) {
            inventory = Inventory.builder()
                    .productId(product != null ? product.getId() : request.getProductId())
                    .variantId(variant.getId())
                    .quantity(newStock)
                    .reservedQuantity(0)
                    .warehouseLocation(request.getWarehouseLocation() != null ? request.getWarehouseLocation() : "Unit #1 Mara Chekku Shed")
                    .build();
        } else {
            inventory.setQuantity(newStock);
            if (request.getWarehouseLocation() != null) {
                inventory.setWarehouseLocation(request.getWarehouseLocation());
            }
        }
        inventoryRepository.save(inventory);

        // Log movement
        String refId = request.getReferenceId() != null
                ? request.getReferenceId()
                : "ADJ-" + (System.currentTimeMillis() % 1000000);

        String performedBy = request.getPerformedBy() != null ? request.getPerformedBy() : "Inventory Head";

        StockMovement movement = StockMovement.builder()
                .productId(product != null ? product.getId() : request.getProductId())
                .variantId(variant.getId())
                .movementType(request.getType() != null ? request.getType() : "ADJUSTMENT")
                .quantity(request.getQuantityChange())
                .previousQuantity(previousStock)
                .newQuantity(newStock)
                .reason(request.getReason() != null ? request.getReason() : "Manual stock adjustment")
                .referenceId(refId)
                .build();
        movement.setCreatedBy(performedBy);
        stockMovementRepository.save(movement);

        int reorderLevel = 20;
        String status = "IN_STOCK";
        if (newStock <= 0) {
            status = "OUT_OF_STOCK";
        } else if (newStock <= reorderLevel) {
            status = "LOW_STOCK";
        }

        String primaryImg = (product != null && product.getImages() != null && !product.getImages().isEmpty())
                ? product.getImages().get(0) : "";

        return StockInventoryItemDto.builder()
                .productId(product != null ? product.getId() : request.getProductId())
                .variantId(variant.getId())
                .productName(product != null ? product.getName() : "Product")
                .primaryImage(primaryImg)
                .brand(product != null && product.getBrand() != null ? product.getBrand().getName() : "Nisha Pure Oils")
                .category(product != null && product.getCategory() != null ? product.getCategory().getName() : "Edible Oils")
                .sku(variant.getSku())
                .variantSize(variant.getCode() != null ? variant.getCode() : variant.getLabel())
                .sellingPrice(variant.getSellingPrice() != null ? variant.getSellingPrice().doubleValue() : 0.0)
                .mrp(variant.getMrp() != null ? variant.getMrp().doubleValue() : 0.0)
                .stockQuantity(newStock)
                .reorderLevel(reorderLevel)
                .batchNumber("MC-" + (product != null ? product.getId().toString().substring(0, 4).toUpperCase() : "PROD") + "-B1")
                .warehouseLocation(inventory.getWarehouseLocation())
                .status(status)
                .build();
    }

    @Transactional(readOnly = true)
    public List<StockMovementDto> getRecentMovements() {
        return stockMovementRepository.findTop100ByDeletedFalseOrderByCreatedAtDesc().stream()
                .map(this::mapToMovementDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<StockMovementDto> getMovementsByProductId(UUID productId) {
        return stockMovementRepository.findByProductIdAndDeletedFalseOrderByCreatedAtDesc(productId).stream()
                .map(this::mapToMovementDto)
                .collect(Collectors.toList());
    }

    private StockMovementDto mapToMovementDto(StockMovement m) {
        String prodName = m.getProduct() != null ? m.getProduct().getName() : "Mara Chekku Cold Pressed Oil";
        String sku = m.getVariant() != null ? m.getVariant().getSku() : "OIL-SKU";
        String size = m.getVariant() != null ? m.getVariant().getCode() : "1L";

        return StockMovementDto.builder()
                .id(m.getId())
                .productId(m.getProductId())
                .variantId(m.getVariantId())
                .productName(prodName)
                .sku(sku)
                .variantSize(size)
                .type(m.getMovementType())
                .quantity(m.getQuantity())
                .previousStock(m.getPreviousQuantity())
                .newStock(m.getNewQuantity())
                .warehouseLocation("Unit #1 Mara Chekku Shed")
                .reason(m.getReason())
                .referenceId(m.getReferenceId())
                .performedBy(m.getCreatedBy() != null ? m.getCreatedBy() : "Warehouse Manager")
                .createdAt(m.getCreatedAt() != null ? m.getCreatedAt().toString() : Instant.now().toString())
                .build();
    }
}
