-- V27: Convert table-level unique constraints to partial unique indexes (WHERE deleted = FALSE)
-- and clean up any soft-deleted products so SKUs and slugs can be freely reused.

DO $$
BEGIN
    -- 1. Clean up conflicting NPO-CO products and variants completely
    BEGIN
        DELETE FROM stock_movements 
        WHERE product_id IN (SELECT id FROM products WHERE UPPER(sku) LIKE 'NPO-CO%')
           OR variant_id IN (SELECT id FROM product_variants WHERE UPPER(sku) LIKE 'NPO-CO%');
    EXCEPTION WHEN OTHERS THEN NULL; END;

    BEGIN
        DELETE FROM inventory 
        WHERE product_id IN (SELECT id FROM products WHERE UPPER(sku) LIKE 'NPO-CO%')
           OR variant_id IN (SELECT id FROM product_variants WHERE UPPER(sku) LIKE 'NPO-CO%');
    EXCEPTION WHEN OTHERS THEN NULL; END;

    BEGIN
        DELETE FROM cart_items 
        WHERE product_id IN (SELECT id FROM products WHERE UPPER(sku) LIKE 'NPO-CO%')
           OR product_variant_id IN (SELECT id FROM product_variants WHERE UPPER(sku) LIKE 'NPO-CO%');
    EXCEPTION WHEN OTHERS THEN NULL; END;

    BEGIN
        DELETE FROM wishlists WHERE product_id IN (SELECT id FROM products WHERE UPPER(sku) LIKE 'NPO-CO%');
    EXCEPTION WHEN OTHERS THEN NULL; END;

    BEGIN
        DELETE FROM reviews WHERE product_id IN (SELECT id FROM products WHERE UPPER(sku) LIKE 'NPO-CO%');
    EXCEPTION WHEN OTHERS THEN NULL; END;

    BEGIN
        DELETE FROM order_items 
        WHERE product_id IN (SELECT id::text FROM products WHERE UPPER(sku) LIKE 'NPO-CO%')
           OR variant_id IN (SELECT id::text FROM product_variants WHERE UPPER(sku) LIKE 'NPO-CO%');
    EXCEPTION WHEN OTHERS THEN NULL; END;

    BEGIN
        DELETE FROM product_variants WHERE product_id IN (SELECT id FROM products WHERE UPPER(sku) LIKE 'NPO-CO%');
    EXCEPTION WHEN OTHERS THEN NULL; END;

    BEGIN
        DELETE FROM products WHERE UPPER(sku) LIKE 'NPO-CO%';
    EXCEPTION WHEN OTHERS THEN NULL; END;

    -- 2. Rename SKUs of any other soft-deleted products so they can never conflict
    BEGIN
        UPDATE products 
        SET sku = sku || '-del-' || SUBSTRING(id::text FROM 1 FOR 8),
            slug = slug || '-del-' || SUBSTRING(id::text FROM 1 FOR 8)
        WHERE deleted = TRUE AND sku NOT LIKE '%-del-%';
    EXCEPTION WHEN OTHERS THEN NULL; END;

    BEGIN
        UPDATE product_variants 
        SET sku = sku || '-del-' || SUBSTRING(id::text FROM 1 FOR 8)
        WHERE deleted = TRUE AND sku NOT LIKE '%-del-%';
    EXCEPTION WHEN OTHERS THEN NULL; END;

    -- 3. Drop rigid table-level constraints on products
    BEGIN
        ALTER TABLE products DROP CONSTRAINT IF EXISTS products_sku_key;
    EXCEPTION WHEN OTHERS THEN NULL; END;

    BEGIN
        ALTER TABLE products DROP CONSTRAINT IF EXISTS products_slug_key;
    EXCEPTION WHEN OTHERS THEN NULL; END;

    BEGIN
        ALTER TABLE product_variants DROP CONSTRAINT IF EXISTS product_variants_sku_key;
    EXCEPTION WHEN OTHERS THEN NULL; END;

    -- 4. Create partial unique indexes (only active records must have unique SKU/slug)
    BEGIN
        CREATE UNIQUE INDEX IF NOT EXISTS idx_products_sku_active_unique ON products(sku) WHERE deleted = FALSE;
    EXCEPTION WHEN OTHERS THEN NULL; END;

    BEGIN
        CREATE UNIQUE INDEX IF NOT EXISTS idx_products_slug_active_unique ON products(slug) WHERE deleted = FALSE;
    EXCEPTION WHEN OTHERS THEN NULL; END;

    BEGIN
        CREATE UNIQUE INDEX IF NOT EXISTS idx_product_variants_sku_active_unique ON product_variants(sku) WHERE deleted = FALSE;
    EXCEPTION WHEN OTHERS THEN NULL; END;

END $$;
