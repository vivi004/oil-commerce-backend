-- V19: Seed complete staff roster and customer profiles into PostgreSQL
DO $$
DECLARE
    -- Password hash for 'Admin@123' (BCrypt strength 12)
    pwd_hash VARCHAR(255) := '$2a$12$5X1/weiPFbwaEt0.ee4rbOsxZ6Tplw.buADQKce/YTHA4AvFcPv0u';

    u_kavitha_id UUID := gen_random_uuid();
    u_anand_id UUID := gen_random_uuid();
    u_deepa_id UUID := gen_random_uuid();
    u_karthik_id UUID := gen_random_uuid();
    u_suresh_id UUID := gen_random_uuid();
BEGIN
    -- 1. SEED ALL ADMINISTRATIVE STAFF ROLES
    INSERT INTO users (id, first_name, last_name, email, password, phone, role, email_verified, active)
    VALUES
        (gen_random_uuid(), 'Super', 'Admin', 'admin@oilcommerce.in', pwd_hash, '+91 98421 00000', 'SUPER_ADMIN', TRUE, TRUE),
        (gen_random_uuid(), 'Gowtham', 'Raj', 'superadmin@nishapureoils.com', pwd_hash, '+91 98421 00009', 'SUPER_ADMIN', TRUE, TRUE),
        (gen_random_uuid(), 'Kaviarasu', 'M', 'admin@nishapureoils.com', pwd_hash, '+91 98421 00001', 'TENANT_ADMIN', TRUE, TRUE),
        (gen_random_uuid(), 'Muruganathan', 'S.', 'admin@varshinigold.com', pwd_hash, '+91 98421 00004', 'TENANT_ADMIN', TRUE, TRUE),
        (gen_random_uuid(), 'Nisha', 'Operations', 'admin@pureoils.com', pwd_hash, '+91 98421 00005', 'TENANT_ADMIN', TRUE, TRUE),
        (gen_random_uuid(), 'R.', 'Velumani', 'warehouse@pureoils.com', pwd_hash, '+91 98421 00002', 'INVENTORY_MANAGER', TRUE, TRUE),
        (gen_random_uuid(), 'Praveen', 'Kumar', 'orders@pureoils.com', pwd_hash, '+91 98421 00003', 'ORDER_MANAGER', TRUE, TRUE),
        (gen_random_uuid(), 'Senthil', 'Nathan', 'accounts@pureoils.com', pwd_hash, '+91 98421 00006', 'ACCOUNTANT', TRUE, TRUE),
        (gen_random_uuid(), 'Lakshmi', 'Priya', 'support@pureoils.com', pwd_hash, '+91 98421 00007', 'CUSTOMER_SUPPORT', TRUE, TRUE)
    ON CONFLICT (email) DO UPDATE SET
        first_name = EXCLUDED.first_name,
        last_name = EXCLUDED.last_name,
        phone = EXCLUDED.phone,
        role = EXCLUDED.role,
        password = EXCLUDED.password,
        active = TRUE,
        email_verified = TRUE;

    -- 2. SEED VERIFIED STOREFRONT CUSTOMERS
    INSERT INTO users (id, first_name, last_name, email, password, phone, role, email_verified, active)
    VALUES
        (u_kavitha_id, 'Kavitha', 'Sundaram', 'kavitha.s@gmail.com', pwd_hash, '+91 98421 88442', 'CUSTOMER', TRUE, TRUE),
        (u_anand_id, 'Anandapadmanabhan', 'R', 'anand.p@gmail.com', pwd_hash, '+91 98421 77654', 'CUSTOMER', TRUE, TRUE),
        (u_deepa_id, 'Deepa', 'Meenakshi', 'deepa.m@yahoo.com', pwd_hash, '+91 94432 99012', 'CUSTOMER', TRUE, TRUE),
        (u_karthik_id, 'Karthikeyan', 'Subramanian', 'karthik.sub@outlook.com', pwd_hash, '+91 98944 11223', 'CUSTOMER', TRUE, TRUE),
        (u_suresh_id, 'Suresh', 'Balaji', 'suresh.b@gmail.com', pwd_hash, '+91 98422 33445', 'CUSTOMER', TRUE, TRUE)
    ON CONFLICT (email) DO UPDATE SET
        first_name = EXCLUDED.first_name,
        last_name = EXCLUDED.last_name,
        phone = EXCLUDED.phone,
        active = TRUE,
        email_verified = TRUE;

    -- Retrieve IDs in case they already existed
    SELECT id INTO u_kavitha_id FROM users WHERE email = 'kavitha.s@gmail.com' LIMIT 1;
    SELECT id INTO u_anand_id FROM users WHERE email = 'anand.p@gmail.com' LIMIT 1;
    SELECT id INTO u_deepa_id FROM users WHERE email = 'deepa.m@yahoo.com' LIMIT 1;
    SELECT id INTO u_karthik_id FROM users WHERE email = 'karthik.sub@outlook.com' LIMIT 1;
    SELECT id INTO u_suresh_id FROM users WHERE email = 'suresh.b@gmail.com' LIMIT 1;

    -- 3. SEED DEFAULT SHIPPING ADDRESSES FOR CUSTOMERS
    IF u_kavitha_id IS NOT NULL THEN
        INSERT INTO addresses (user_id, label, full_name, phone, address_line1, city, state, postal_code, country, default_address)
        VALUES (u_kavitha_id, 'Home', 'Kavitha Sundaram', '+91 98421 88442', '42, Cross Cut Road, Gandhipuram', 'Coimbatore', 'Tamil Nadu', '641012', 'India', TRUE)
        ON CONFLICT DO NOTHING;
    END IF;

    IF u_anand_id IS NOT NULL THEN
        INSERT INTO addresses (user_id, label, full_name, phone, address_line1, city, state, postal_code, country, default_address)
        VALUES (u_anand_id, 'Home', 'Anandapadmanabhan R', '+91 98421 77654', '42, Perundurai Road', 'Erode', 'Tamil Nadu', '638011', 'India', TRUE)
        ON CONFLICT DO NOTHING;
    END IF;

    IF u_deepa_id IS NOT NULL THEN
        INSERT INTO addresses (user_id, label, full_name, phone, address_line1, city, state, postal_code, country, default_address)
        VALUES (u_deepa_id, 'Office', 'Deepa Meenakshi', '+91 94432 99012', '15, Anna Nagar 2nd Street', 'Tirupur', 'Tamil Nadu', '641602', 'India', TRUE)
        ON CONFLICT DO NOTHING;
    END IF;

    IF u_karthik_id IS NOT NULL THEN
        INSERT INTO addresses (user_id, label, full_name, phone, address_line1, city, state, postal_code, country, default_address)
        VALUES (u_karthik_id, 'Store', 'Karthikeyan Subramanian', '+91 98944 11223', '8B, Lakshmi Mills Colony', 'Coimbatore', 'Tamil Nadu', '641037', 'India', TRUE)
        ON CONFLICT DO NOTHING;
    END IF;

    IF u_suresh_id IS NOT NULL THEN
        INSERT INTO addresses (user_id, label, full_name, phone, address_line1, city, state, postal_code, country, default_address)
        VALUES (u_suresh_id, 'Home', 'Suresh Balaji', '+91 98422 33445', '124, Five Roads Junction', 'Salem', 'Tamil Nadu', '636004', 'India', TRUE)
        ON CONFLICT DO NOTHING;
    END IF;
END $$;
