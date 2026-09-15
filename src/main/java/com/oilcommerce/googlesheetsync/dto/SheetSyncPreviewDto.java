package com.oilcommerce.googlesheetsync.dto;

import java.math.BigDecimal;

public class SheetSyncPreviewDto {
    private String productId;
    private String productName;
    private String variantCode;
    private BigDecimal currentPrice;
    private BigDecimal newPrice;
    private String status;

    public SheetSyncPreviewDto() {}

    public SheetSyncPreviewDto(String productId, String productName, String variantCode,
                               BigDecimal currentPrice, BigDecimal newPrice, String status) {
        this.productId = productId;
        this.productName = productName;
        this.variantCode = variantCode;
        this.currentPrice = currentPrice;
        this.newPrice = newPrice;
        this.status = status;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String productId;
        private String productName;
        private String variantCode;
        private BigDecimal currentPrice;
        private BigDecimal newPrice;
        private String status;

        public Builder productId(String productId) {
            this.productId = productId;
            return this;
        }

        public Builder productName(String productName) {
            this.productName = productName;
            return this;
        }

        public Builder variantCode(String variantCode) {
            this.variantCode = variantCode;
            return this;
        }

        public Builder currentPrice(BigDecimal currentPrice) {
            this.currentPrice = currentPrice;
            return this;
        }

        public Builder newPrice(BigDecimal newPrice) {
            this.newPrice = newPrice;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public SheetSyncPreviewDto build() {
            return new SheetSyncPreviewDto(productId, productName, variantCode, currentPrice, newPrice, status);
        }
    }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getVariantCode() { return variantCode; }
    public void setVariantCode(String variantCode) { this.variantCode = variantCode; }

    public BigDecimal getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(BigDecimal currentPrice) { this.currentPrice = currentPrice; }

    public BigDecimal getNewPrice() { return newPrice; }
    public void setNewPrice(BigDecimal newPrice) { this.newPrice = newPrice; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
