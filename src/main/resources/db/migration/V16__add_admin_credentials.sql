-- V16: Add working hashed passwords for Super Admin and Tenant Admin
UPDATE users 
SET password = '$2a$12$5X1/weiPFbwaEt0.ee4rbOsxZ6Tplw.buADQKce/YTHA4AvFcPv0u'
WHERE email = 'admin@oilcommerce.in';

INSERT INTO users (id, first_name, last_name, email, password, role, email_verified, active)
VALUES 
    (gen_random_uuid(), 'Kaviarasu', 'M', 'admin@nishapureoils.com', '$2a$12$5X1/weiPFbwaEt0.ee4rbOsxZ6Tplw.buADQKce/YTHA4AvFcPv0u', 'TENANT_ADMIN', TRUE, TRUE),
    (gen_random_uuid(), 'Tenant', 'Admin', 'admin@pureoils.com', '$2a$12$5X1/weiPFbwaEt0.ee4rbOsxZ6Tplw.buADQKce/YTHA4AvFcPv0u', 'TENANT_ADMIN', TRUE, TRUE),
    (gen_random_uuid(), 'Gowtham', 'Raj', 'superadmin@nishapureoils.com', '$2a$12$5X1/weiPFbwaEt0.ee4rbOsxZ6Tplw.buADQKce/YTHA4AvFcPv0u', 'SUPER_ADMIN', TRUE, TRUE)
ON CONFLICT (email) DO UPDATE SET password = EXCLUDED.password, role = EXCLUDED.role, active = TRUE, email_verified = TRUE;
