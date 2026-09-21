-- V23: Remove all sample/demo users and demo orders from PostgreSQL, retaining only authentic accounts
DO $$
BEGIN
    -- 1. Remove order items and histories belonging to demo customers
    DELETE FROM order_items WHERE order_id IN (
        SELECT id FROM orders WHERE user_id IN (
            SELECT id FROM users WHERE email IN (
                'kavitha.s@gmail.com',
                'anand.p@gmail.com',
                'deepa.m@yahoo.com',
                'karthik.sub@outlook.com',
                'suresh.b@gmail.com'
            )
        )
    );

    DELETE FROM order_status_history WHERE order_id IN (
        SELECT id FROM orders WHERE user_id IN (
            SELECT id FROM users WHERE email IN (
                'kavitha.s@gmail.com',
                'anand.p@gmail.com',
                'deepa.m@yahoo.com',
                'karthik.sub@outlook.com',
                'suresh.b@gmail.com'
            )
        )
    );

    DELETE FROM payments WHERE order_id IN (
        SELECT id FROM orders WHERE user_id IN (
            SELECT id FROM users WHERE email IN (
                'kavitha.s@gmail.com',
                'anand.p@gmail.com',
                'deepa.m@yahoo.com',
                'karthik.sub@outlook.com',
                'suresh.b@gmail.com'
            )
        )
    );

    DELETE FROM orders WHERE user_id IN (
        SELECT id FROM users WHERE email IN (
            'kavitha.s@gmail.com',
            'anand.p@gmail.com',
            'deepa.m@yahoo.com',
            'karthik.sub@outlook.com',
            'suresh.b@gmail.com'
        )
    );

    DELETE FROM addresses WHERE user_id IN (
        SELECT id FROM users WHERE email IN (
            'kavitha.s@gmail.com',
            'anand.p@gmail.com',
            'deepa.m@yahoo.com',
            'karthik.sub@outlook.com',
            'suresh.b@gmail.com'
        )
    );

    DELETE FROM cart_items WHERE user_id IN (
        SELECT id FROM users WHERE email IN (
            'kavitha.s@gmail.com',
            'anand.p@gmail.com',
            'deepa.m@yahoo.com',
            'karthik.sub@outlook.com',
            'suresh.b@gmail.com'
        )
    );

    DELETE FROM wishlist_items WHERE user_id IN (
        SELECT id FROM users WHERE email IN (
            'kavitha.s@gmail.com',
            'anand.p@gmail.com',
            'deepa.m@yahoo.com',
            'karthik.sub@outlook.com',
            'suresh.b@gmail.com'
        )
    );

    -- 2. Remove all sample staff and sample customer records
    DELETE FROM users WHERE email IN (
        'kavitha.s@gmail.com',
        'anand.p@gmail.com',
        'deepa.m@yahoo.com',
        'karthik.sub@outlook.com',
        'suresh.b@gmail.com',
        'warehouse@pureoils.com',
        'orders@pureoils.com',
        'accounts@pureoils.com',
        'support@pureoils.com',
        'admin@varshinigold.com',
        'admin@pureoils.com'
    );
END $$;
