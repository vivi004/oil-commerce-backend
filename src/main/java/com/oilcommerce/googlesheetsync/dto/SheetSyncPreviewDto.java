package com.oilcommerce.googlesheetsync.dto;

import java.math.BigDecimal;

/**
 * DTO representing a single variant-level diff between the Google Sheet price
 * and the current live price in the database.
 */
public class SheetSyncPreviewDto {

    // --- Variant identification ---
    private String productId;
    private String productName;
    private String sku;           // variant SKU, e.g. NPO-GNO-1L
    private String variantSize;   // e.g. "1L", "500ml", "5Kg"

    // --- Current DB values ---
    private BigDecimal currentPrice;      // current sellingPrice from DB
    private BigDecimal mrp;               // current MRP from DB (unchanged by sheet)
    private int currentStock;             // current stock from DB

    // --- Sheet-sourced new values ---
    private BigDecimal newPrice;          // sheet sellingPrice
    private BigDecimal newMrp;            // same as mrp (sheet has no MRP col)

    // --- Computed ---
    private BigDecimal priceDelta;        // newPrice - currentPrice

    // --- Workflow ---
    private String status;                // "PENDING", "APPROVED", "SYNCED"
    private boolean approved;

    public SheetSyncPreviewDto() {}

    private SheetSyncPreviewDto(Builder b) {
        this.productId    = b.productId;
        this.productName  = b.productName;
        this.sku          = b.sku;
        this.variantSize  = b.variantSize;
        this.currentPrice = b.currentPrice;
        this.mrp          = b.mrp;
        this.currentStock = b.currentStock;
        this.newPrice     = b.newPrice;
        this.newMrp       = b.newMrp;
        this.priceDelta   = b.priceDelta;
        this.status       = b.status;
        this.approved     = b.approved;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String productId, productName, sku, variantSize, status;
        private BigDecimal currentPrice, mrp, newPrice, newMrp, priceDelta;
        private int currentStock;
        private boolean approved;

        public Builder productId(String v)      { this.productId = v; return this; }
        public Builder productName(String v)    { this.productName = v; return this; }
        public Builder sku(String v)            { this.sku = v; return this; }
        public Builder variantSize(String v)    { this.variantSize = v; return this; }
        public Builder currentPrice(BigDecimal v) { this.currentPrice = v; return this; }
        public Builder mrp(BigDecimal v)        { this.mrp = v; return this; }
        public Builder currentStock(int v)      { this.currentStock = v; return this; }
        public Builder newPrice(BigDecimal v)   { this.newPrice = v; return this; }
        public Builder newMrp(BigDecimal v)     { this.newMrp = v; return this; }
        public Builder priceDelta(BigDecimal v) { this.priceDelta = v; return this; }
        public Builder status(String v)         { this.status = v; return this; }
        public Builder approved(boolean v)      { this.approved = v; return this; }
        public SheetSyncPreviewDto build()      { return new SheetSyncPreviewDto(this); }
    }

    // --- Getters & Setters ---
    public String getProductId()         { return productId; }
    public void setProductId(String v)   { this.productId = v; }

    public String getProductName()       { return productName; }
    public void setProductName(String v) { this.productName = v; }

    public String getSku()               { return sku; }
    public void setSku(String v)         { this.sku = v; }

    public String getVariantSize()       { return variantSize; }
    public void setVariantSize(String v) { this.variantSize = v; }

    public BigDecimal getCurrentPrice()       { return currentPrice; }
    public void setCurrentPrice(BigDecimal v) { this.currentPrice = v; }

    public BigDecimal getMrp()               { return mrp; }
    public void setMrp(BigDecimal v)         { this.mrp = v; }

    public int getCurrentStock()             { return currentStock; }
    public void setCurrentStock(int v)       { this.currentStock = v; }

    public BigDecimal getNewPrice()           { return newPrice; }
    public void setNewPrice(BigDecimal v)     { this.newPrice = v; }

    public BigDecimal getNewMrp()             { return newMrp; }
    public void setNewMrp(BigDecimal v)       { this.newMrp = v; }

    public BigDecimal getPriceDelta()         { return priceDelta; }
    public void setPriceDelta(BigDecimal v)   { this.priceDelta = v; }

    public String getStatus()                { return status; }
    public void setStatus(String v)          { this.status = v; }

    public boolean isApproved()              { return approved; }
    public void setApproved(boolean v)       { this.approved = v; }

    /** Backwards-compat alias used by old code */
    public BigDecimal getVariantCode()       { return currentPrice; }
}
