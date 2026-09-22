-- V26: Remove the incomplete 'NPO-CO' product so the SKU can be reused from the admin panel
DO $$
DECLARE
    v_product_id UUID;
BEGIN
    -- Find the product with SKU 'NPO-CO'
    SELECT id INTO v_product_id
    FROM products
    WHERE sku = 'NPO-CO' AND deleted = FALSE
    LIMIT 1;

    IF v_product_id IS NOT NULL THEN
        -- Remove inventory / stock movements
        BEGIN
            DELETE FROM stock_movements WHERE variant_id IN (
                SELECT id FROM product_variants WHERE product_id = v_product_id
            );
        EXCEPTION WHEN undefined_table THEN NULL; END;

        BEGIN
            DELETE FROM inventory WHERE product_id = v_product_id;
        EXCEPTION WHEN undefined_table THEN NULL; END;

        -- Remove cart & wishlist references
        BEGIN
            DELETE FROM cart_items WHERE product_id = v_product_id
                OR variant_id IN (SELECT id FROM product_variants WHERE product_id = v_product_id);
        EXCEPTION WHEN undefined_table THEN NULL; END;

        BEGIN
            DELETE FROM wishlists WHERE product_id = v_product_id;
        EXCEPTION WHEN undefined_table THEN NULL; END;

        -- Remove reviews
        BEGIN
            DELETE FROM reviews WHERE product_id = v_product_id;
        EXCEPTION WHEN undefined_table THEN NULL; END;

        -- Remove order items
        BEGIN
            DELETE FROM order_items WHERE product_id = v_product_id
                OR variant_id IN (SELECT id FROM product_variants WHERE product_id = v_product_id);
        EXCEPTION WHEN undefined_table THEN NULL; END;

        -- Remove variants
        BEGIN
            DELETE FROM product_variants WHERE product_id = v_product_id;
        EXCEPTION WHEN undefined_table THEN NULL; END;

        -- Remove the product itself
        DELETE FROM products WHERE id = v_product_id;

        RAISE NOTICE 'Deleted product NPO-CO (id: %)', v_product_id;
    ELSE
        RAISE NOTICE 'Product with SKU NPO-CO not found — nothing to delete.';
    END IF;
END $$;
