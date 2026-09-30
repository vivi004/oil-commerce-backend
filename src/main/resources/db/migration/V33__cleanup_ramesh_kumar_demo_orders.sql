-- V33: Cleanup legacy demo/placeholder orders (e.g. Ramesh Kumar / demo test orders)
DO $$
BEGIN
    -- 1. Remove child records from related tables without ON DELETE CASCADE
    BEGIN
        DELETE FROM coupon_usages WHERE order_id IN (
            SELECT id FROM orders WHERE shipping_address::text ILIKE '%Ramesh Kumar%' OR order_number IN ('ORD-2297-2026', 'ORD-6741-2026', 'ORD-4943-2026')
        );
    EXCEPTION WHEN undefined_table THEN NULL; END;

    BEGIN
        DELETE FROM shipments WHERE order_id IN (
            SELECT id FROM orders WHERE shipping_address::text ILIKE '%Ramesh Kumar%' OR order_number IN ('ORD-2297-2026', 'ORD-6741-2026', 'ORD-4943-2026')
        );
    EXCEPTION WHEN undefined_table THEN NULL; END;

    BEGIN
        DELETE FROM refunds WHERE order_id IN (
            SELECT id FROM orders WHERE shipping_address::text ILIKE '%Ramesh Kumar%' OR order_number IN ('ORD-2297-2026', 'ORD-6741-2026', 'ORD-4943-2026')
        );
    EXCEPTION WHEN undefined_table THEN NULL; END;

    BEGIN
        DELETE FROM payments WHERE order_id IN (
            SELECT id FROM orders WHERE shipping_address::text ILIKE '%Ramesh Kumar%' OR order_number IN ('ORD-2297-2026', 'ORD-6741-2026', 'ORD-4943-2026')
        );
    EXCEPTION WHEN undefined_table THEN NULL; END;

    -- 2. Remove the placeholder orders (order_items and order_status_history cascade delete)
    BEGIN
        DELETE FROM orders WHERE shipping_address::text ILIKE '%Ramesh Kumar%' OR order_number IN ('ORD-2297-2026', 'ORD-6741-2026', 'ORD-4943-2026');
    EXCEPTION WHEN undefined_table THEN NULL; END;
END $$;
