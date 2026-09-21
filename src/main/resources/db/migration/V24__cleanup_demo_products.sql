-- V24: Remove all demo products and demo variants, inventory, and stock movements, and expand image URL column capacity
DO $$
BEGIN
    -- 1. Remove inventory and stock movement records tied to demo products
    BEGIN
        DELETE FROM stock_movements;
    EXCEPTION WHEN undefined_table THEN NULL; END;

    BEGIN
        DELETE FROM inventory;
    EXCEPTION WHEN undefined_table THEN NULL; END;

    -- 2. Remove user cart, wishlist, and review references to products
    BEGIN
        DELETE FROM cart_items;
    EXCEPTION WHEN undefined_table THEN NULL; END;

    BEGIN
        DELETE FROM wishlists;
    EXCEPTION WHEN undefined_table THEN NULL; END;

    BEGIN
        DELETE FROM reviews;
    EXCEPTION WHEN undefined_table THEN NULL; END;

    -- 3. Remove order items tied to demo products
    BEGIN
        DELETE FROM order_items;
    EXCEPTION WHEN undefined_table THEN NULL; END;

    -- 4. Delete all product variants
    BEGIN
        DELETE FROM product_variants;
    EXCEPTION WHEN undefined_table THEN NULL; END;

    -- 5. Delete all demo products
    BEGIN
        DELETE FROM products;
    EXCEPTION WHEN undefined_table THEN NULL; END;

    -- 6. Expand thumbnail & image_url column sizes to TEXT so long URLs/CDN paths never fail
    BEGIN
        ALTER TABLE products ALTER COLUMN thumbnail TYPE TEXT;
    EXCEPTION WHEN undefined_column OR undefined_table THEN NULL; END;

    BEGIN
        ALTER TABLE product_variants ALTER COLUMN image_url TYPE TEXT;
    EXCEPTION WHEN undefined_column OR undefined_table THEN NULL; END;

END $$;
