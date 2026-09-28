-- V31__add_price_sync_audit_to_variants.sql
-- Adds audit columns to track Google Sheet price sync on each product variant.

ALTER TABLE product_variants
    ADD COLUMN IF NOT EXISTS price_synced_at    TIMESTAMPTZ,
    ADD COLUMN IF NOT EXISTS price_sync_source  VARCHAR(50);

-- Index for quick lookup of all sheet-synced variants
CREATE INDEX IF NOT EXISTS idx_product_variants_price_sync_source
    ON product_variants (price_sync_source)
    WHERE price_sync_source IS NOT NULL;

COMMENT ON COLUMN product_variants.price_synced_at   IS 'Timestamp of last Google Sheet price sync for this variant';
COMMENT ON COLUMN product_variants.price_sync_source IS 'Source of last price update: GOOGLE_SHEET or MANUAL';
