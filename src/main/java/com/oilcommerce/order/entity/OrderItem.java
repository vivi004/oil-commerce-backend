package com.oilcommerce.order.entity;

import com.oilcommerce.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity @Table(name = "order_items")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OrderItem extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "order_id", nullable = false) private Order order;
    @Column(nullable = false) private String productId;
    @Column(nullable = false) private String productName;
    @Column private String productImage;
    @Column(nullable = false) private String sku;
    @Column private String variantId;
    @Column private String variantCode;
    @Column(nullable = false) private int quantity;
    @Column(nullable = false, precision = 10, scale = 2) private BigDecimal unitPrice;
    @Column(nullable = false, precision = 10, scale = 2) private BigDecimal totalPrice;

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public String getProductImage() { return productImage; }
    public void setProductImage(String productImage) { this.productImage = productImage; }
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public String getVariantId() { return variantId; }
    public void setVariantId(String variantId) { this.variantId = variantId; }
    public String getVariantCode() { return variantCode; }
    public void setVariantCode(String variantCode) { this.variantCode = variantCode; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }

    public static OrderItemBuilder builder() {
        return new OrderItemBuilder();
    }

    public static class OrderItemBuilder {
        private Order order;
        private String productId;
        private String productName;
        private String productImage;
        private String sku;
        private String variantId;
        private String variantCode;
        private int quantity;
        private BigDecimal unitPrice;
        private BigDecimal totalPrice;

        public OrderItemBuilder order(Order order) { this.order = order; return this; }
        public OrderItemBuilder productId(String productId) { this.productId = productId; return this; }
        public OrderItemBuilder productName(String productName) { this.productName = productName; return this; }
        public OrderItemBuilder productImage(String productImage) { this.productImage = productImage; return this; }
        public OrderItemBuilder sku(String sku) { this.sku = sku; return this; }
        public OrderItemBuilder variantId(String variantId) { this.variantId = variantId; return this; }
        public OrderItemBuilder variantCode(String variantCode) { this.variantCode = variantCode; return this; }
        public OrderItemBuilder quantity(int quantity) { this.quantity = quantity; return this; }
        public OrderItemBuilder unitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; return this; }
        public OrderItemBuilder totalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; return this; }

        public OrderItem build() {
            OrderItem item = new OrderItem();
            item.setOrder(this.order);
            item.setProductId(this.productId);
            item.setProductName(this.productName);
            item.setProductImage(this.productImage);
            item.setSku(this.sku);
            item.setVariantId(this.variantId);
            item.setVariantCode(this.variantCode);
            item.setQuantity(this.quantity);
            item.setUnitPrice(this.unitPrice);
            item.setTotalPrice(this.totalPrice);
            return item;
        }
    }
}
