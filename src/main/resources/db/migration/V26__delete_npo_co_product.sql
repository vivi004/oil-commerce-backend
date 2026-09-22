-- V26: Remove the incomplete 'NPO-CO' product and old castor records so the SKU can be reused cleanly
DO $$
DECLARE
    rec RECORD;
BEGIN
    FOR rec IN (
        SELECT id FROM products 
        WHERE UPPER(sku) IN ('NPO-CO', 'NPO-CAS-004') 
           OR slug = 'castor-oil'
    ) LOOP
        -- 1. Remove inventory & stock movements
        BEGIN
            DELETE FROM stock_movements 
            WHERE product_id = rec.id 
               OR variant_id IN (SELECT id FROM product_variants WHERE product_id = rec.id);
        EXCEPTION WHEN OTHERS THEN NULL; END;

        BEGIN
            DELETE FROM inventory 
            WHERE product_id = rec.id 
               OR variant_id IN (SELECT id FROM product_variants WHERE product_id = rec.id);
        EXCEPTION WHEN OTHERS THEN NULL; END;

        -- 2. Remove cart items (column is product_variant_id)
        BEGIN
            DELETE FROM cart_items 
            WHERE product_id = rec.id
               OR product_variant_id IN (SELECT id FROM product_variants WHERE product_id = rec.id);
        EXCEPTION WHEN OTHERS THEN NULL; END;

        -- 3. Remove wishlists
        BEGIN
            DELETE FROM wishlists WHERE product_id = rec.id;
        EXCEPTION WHEN OTHERS THEN NULL; END;

        -- 4. Remove reviews
        BEGIN
            DELETE FROM reviews WHERE product_id = rec.id;
        EXCEPTION WHEN OTHERS THEN NULL; END;

        -- 5. Remove order items (product_id and variant_id are varchar)
        BEGIN
            DELETE FROM order_items 
            WHERE product_id = rec.id::text
               OR variant_id IN (SELECT id::text FROM product_variants WHERE product_id = rec.id);
        EXCEPTION WHEN OTHERS THEN NULL; END;

        -- 6. Remove product variants
        BEGIN
            DELETE FROM product_variants WHERE product_id = rec.id;
        EXCEPTION WHEN OTHERS THEN NULL; END;

        -- 7. Remove the product itself
        BEGIN
            DELETE FROM products WHERE id = rec.id;
        EXCEPTION WHEN OTHERS THEN NULL; END;

        RAISE NOTICE 'Deleted conflicting product id: %', rec.id;
    END LOOP;
END $$;
