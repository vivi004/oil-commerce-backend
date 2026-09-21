-- V23: Remove all sample/demo users and demo orders from PostgreSQL, retaining only authentic accounts
DO $$
DECLARE
    demo_emails TEXT[] := ARRAY[
        'kavitha.s@gmail.com',
        'anand.p@gmail.com',
        'deepa.m@yahoo.com',
        'karthik.sub@outlook.com',
        'suresh.b@gmail.com'
    ];
    staff_emails TEXT[] := ARRAY[
        'warehouse@pureoils.com',
        'orders@pureoils.com',
        'accounts@pureoils.com',
        'support@pureoils.com',
        'admin@varshinigold.com',
        'admin@pureoils.com'
    ];
BEGIN
    -- 1. Remove order dependencies first
    BEGIN
        DELETE FROM coupon_usages WHERE order_id IN (
            SELECT id FROM orders WHERE user_id IN (SELECT id FROM users WHERE email = ANY(demo_emails))
        ) OR user_id IN (SELECT id FROM users WHERE email = ANY(demo_emails));
    EXCEPTION WHEN undefined_table THEN NULL; END;

    BEGIN
        DELETE FROM shipments WHERE order_id IN (
            SELECT id FROM orders WHERE user_id IN (SELECT id FROM users WHERE email = ANY(demo_emails))
        );
    EXCEPTION WHEN undefined_table THEN NULL; END;

    BEGIN
        DELETE FROM refunds WHERE order_id IN (
            SELECT id FROM orders WHERE user_id IN (SELECT id FROM users WHERE email = ANY(demo_emails))
        );
    EXCEPTION WHEN undefined_table THEN NULL; END;

    BEGIN
        DELETE FROM payments WHERE order_id IN (
            SELECT id FROM orders WHERE user_id IN (SELECT id FROM users WHERE email = ANY(demo_emails))
        );
    EXCEPTION WHEN undefined_table THEN NULL; END;

    BEGIN
        DELETE FROM order_items WHERE order_id IN (
            SELECT id FROM orders WHERE user_id IN (SELECT id FROM users WHERE email = ANY(demo_emails))
        );
    EXCEPTION WHEN undefined_table THEN NULL; END;

    BEGIN
        DELETE FROM order_status_history WHERE order_id IN (
            SELECT id FROM orders WHERE user_id IN (SELECT id FROM users WHERE email = ANY(demo_emails))
        );
    EXCEPTION WHEN undefined_table THEN NULL; END;

    BEGIN
        DELETE FROM orders WHERE user_id IN (SELECT id FROM users WHERE email = ANY(demo_emails));
    EXCEPTION WHEN undefined_table THEN NULL; END;

    -- 2. Remove user dependencies
    BEGIN
        DELETE FROM reviews WHERE user_id IN (SELECT id FROM users WHERE email = ANY(demo_emails));
    EXCEPTION WHEN undefined_table THEN NULL; END;

    BEGIN
        DELETE FROM addresses WHERE user_id IN (SELECT id FROM users WHERE email = ANY(demo_emails));
    EXCEPTION WHEN undefined_table THEN NULL; END;

    BEGIN
        DELETE FROM cart_items WHERE user_id IN (SELECT id FROM users WHERE email = ANY(demo_emails));
    EXCEPTION WHEN undefined_table THEN NULL; END;

    BEGIN
        DELETE FROM wishlists WHERE user_id IN (SELECT id FROM users WHERE email = ANY(demo_emails));
    EXCEPTION WHEN undefined_table THEN NULL; END;

    BEGIN
        DELETE FROM notifications WHERE user_id IN (SELECT id FROM users WHERE email = ANY(demo_emails));
    EXCEPTION WHEN undefined_table THEN NULL; END;

    -- 3. Remove all sample staff and sample customer records
    BEGIN
        DELETE FROM users WHERE email = ANY(demo_emails) OR email = ANY(staff_emails);
    EXCEPTION WHEN undefined_table THEN NULL; END;
END $$;
