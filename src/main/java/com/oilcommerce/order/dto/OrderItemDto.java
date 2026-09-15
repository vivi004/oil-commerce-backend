package com.oilcommerce.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDto {
    private UUID id;
    private String productId;
    private String productName;
    private String productImage;
    private String sku;
    private String productSku;
    private String variantId;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public String getProductImage() { return productImage; }
    public void setProductImage(String productImage) { this.productImage = productImage; }
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public String getProductSku() { return productSku; }
    public void setProductSku(String productSku) { this.productSku = productSku; }
    public String getVariantId() { return variantId; }
    public void setVariantId(String variantId) { this.variantId = variantId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }

    public static OrderItemDtoBuilder builder() {
        return new OrderItemDtoBuilder();
    }

    public static class OrderItemDtoBuilder {
        private UUID id;
        private String productId;
        private String productName;
        private String productImage;
        private String sku;
        private String productSku;
        private String variantId;
        private int quantity;
        private BigDecimal unitPrice;
        private BigDecimal totalPrice;

        public OrderItemDtoBuilder id(UUID id) { this.id = id; return this; }
        public OrderItemDtoBuilder productId(String productId) { this.productId = productId; return this; }
        public OrderItemDtoBuilder productName(String productName) { this.productName = productName; return this; }
        public OrderItemDtoBuilder productImage(String productImage) { this.productImage = productImage; return this; }
        public OrderItemDtoBuilder sku(String sku) { this.sku = sku; return this; }
        public OrderItemDtoBuilder productSku(String productSku) { this.productSku = productSku; return this; }
        public OrderItemDtoBuilder variantId(String variantId) { this.variantId = variantId; return this; }
        public OrderItemDtoBuilder quantity(int quantity) { this.quantity = quantity; return this; }
        public OrderItemDtoBuilder unitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; return this; }
        public OrderItemDtoBuilder totalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; return this; }

        public OrderItemDto build() {
            OrderItemDto dto = new OrderItemDto();
            dto.setId(this.id);
            dto.setProductId(this.productId);
            dto.setProductName(this.productName);
            dto.setProductImage(this.productImage);
            dto.setSku(this.sku);
            dto.setProductSku(this.productSku);
            dto.setVariantId(this.variantId);
            dto.setQuantity(this.quantity);
            dto.setUnitPrice(this.unitPrice);
            dto.setTotalPrice(this.totalPrice);
            return dto;
        }
    }
}
