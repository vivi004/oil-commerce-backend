-- V25: Remove all demo customer orders and order dependencies from PostgreSQL
DO $$
DECLARE
    demo_order_prefixes TEXT[] := ARRAY['NPO-2025-%', 'ord-%', 'DEMO-%'];
    demo_customer_emails TEXT[] := ARRAY[
        'anand.p@gmail.com',
        'deepa.m@yahoo.com',
        'karthik.sub@outlook.com',
        'kavitha.s@gmail.com',
        'suresh.b@gmail.com'
    ];
BEGIN
    -- 1. Remove order dependencies first
    BEGIN
        DELETE FROM coupon_usages WHERE order_id IN (
            SELECT id FROM orders WHERE order_number LIKE ANY(demo_order_prefixes)
            OR customer_email = ANY(demo_customer_emails)
        );
    EXCEPTION WHEN undefined_table THEN NULL; END;

    BEGIN
        DELETE FROM shipments WHERE order_id IN (
            SELECT id FROM orders WHERE order_number LIKE ANY(demo_order_prefixes)
            OR customer_email = ANY(demo_customer_emails)
        );
    EXCEPTION WHEN undefined_table THEN NULL; END;

    BEGIN
        DELETE FROM refunds WHERE order_id IN (
            SELECT id FROM orders WHERE order_number LIKE ANY(demo_order_prefixes)
            OR customer_email = ANY(demo_customer_emails)
        );
    EXCEPTION WHEN undefined_table THEN NULL; END;

    BEGIN
        DELETE FROM payments WHERE order_id IN (
            SELECT id FROM orders WHERE order_number LIKE ANY(demo_order_prefixes)
            OR customer_email = ANY(demo_customer_emails)
        );
    EXCEPTION WHEN undefined_table THEN NULL; END;

    BEGIN
        DELETE FROM order_items WHERE order_id IN (
            SELECT id FROM orders WHERE order_number LIKE ANY(demo_order_prefixes)
            OR customer_email = ANY(demo_customer_emails)
        );
    EXCEPTION WHEN undefined_table THEN NULL; END;

    BEGIN
        DELETE FROM order_status_history WHERE order_id IN (
            SELECT id FROM orders WHERE order_number LIKE ANY(demo_order_prefixes)
            OR customer_email = ANY(demo_customer_emails)
        );
    EXCEPTION WHEN undefined_table THEN NULL; END;

    -- 2. Remove demo orders
    BEGIN
        DELETE FROM orders WHERE order_number LIKE ANY(demo_order_prefixes)
        OR customer_email = ANY(demo_customer_emails);
    EXCEPTION WHEN undefined_table THEN NULL; END;
END $$;
