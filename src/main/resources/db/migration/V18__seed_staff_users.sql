-- V18: Seed administrative staff users into PostgreSQL
INSERT INTO users (id, first_name, last_name, email, password, phone, role, email_verified, active)
VALUES
    (gen_random_uuid(), 'Super', 'Admin', 'admin@oilcommerce.in', '$2a$12$5X1/weiPFbwaEt0.ee4rbOsxZ6Tplw.buADQKce/YTHA4AvFcPv0u', '+91 98421 00000', 'SUPER_ADMIN', TRUE, TRUE),
    (gen_random_uuid(), 'Kaviarasu', 'M', 'admin@nishapureoils.com', '$2a$12$5X1/weiPFbwaEt0.ee4rbOsxZ6Tplw.buADQKce/YTHA4AvFcPv0u', '+91 98421 00001', 'TENANT_ADMIN', TRUE, TRUE),
    (gen_random_uuid(), 'R.', 'Velumani', 'warehouse@pureoils.com', '$2a$12$5X1/weiPFbwaEt0.ee4rbOsxZ6Tplw.buADQKce/YTHA4AvFcPv0u', '+91 98421 00002', 'INVENTORY_MANAGER', TRUE, TRUE),
    (gen_random_uuid(), 'Praveen', 'Kumar', 'orders@pureoils.com', '$2a$12$5X1/weiPFbwaEt0.ee4rbOsxZ6Tplw.buADQKce/YTHA4AvFcPv0u', '+91 98421 00003', 'ORDER_MANAGER', TRUE, TRUE),
    (gen_random_uuid(), 'Muruganathan', 'S.', 'admin@varshinigold.com', '$2a$12$5X1/weiPFbwaEt0.ee4rbOsxZ6Tplw.buADQKce/YTHA4AvFcPv0u', '+91 98421 00004', 'TENANT_ADMIN', TRUE, TRUE)
ON CONFLICT (email) DO UPDATE SET
    first_name = EXCLUDED.first_name,
    last_name = EXCLUDED.last_name,
    phone = EXCLUDED.phone,
    role = EXCLUDED.role,
    password = EXCLUDED.password,
    active = TRUE,
    email_verified = TRUE;
