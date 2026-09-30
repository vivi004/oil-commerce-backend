-- V32: Seed Default Promo Codes and Discount Coupons
INSERT INTO coupons (id, code, description, type, value, minimum_order_amount, maximum_discount, usage_limit, usage_count, per_user_limit, active)
VALUES
    (gen_random_uuid(), 'SAVE20', 'Flat 20% off on all cold-pressed cooking & health oils', 'PERCENT', 20.00, 500.00, 500.00, 10000, 0, 5, TRUE),
    (gen_random_uuid(), 'PURE20', '20% off pure wood-churned Mara Chekku oils', 'PERCENT', 20.00, 400.00, 400.00, 10000, 0, 5, TRUE),
    (gen_random_uuid(), 'WELCOME10', 'Special 10% welcome discount for new wellness customers', 'PERCENT', 10.00, 200.00, 200.00, 50000, 0, 1, TRUE),
    (gen_random_uuid(), 'NISHA10', '10% instant discount on Nisha Pure Oils catalog', 'PERCENT', 10.00, 300.00, 300.00, 10000, 0, 10, TRUE),
    (gen_random_uuid(), 'FIRSTOIL', '15% introductory discount on your first order', 'PERCENT', 15.00, 250.00, 250.00, 10000, 0, 1, TRUE)
ON CONFLICT (code) DO UPDATE SET
    value = EXCLUDED.value,
    minimum_order_amount = EXCLUDED.minimum_order_amount,
    maximum_discount = EXCLUDED.maximum_discount,
    active = TRUE;
