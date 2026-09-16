-- V20: Create subscription plans, enhance tenants table, and link users to tenants

-- 1. Create subscription_plans table
CREATE TABLE IF NOT EXISTS subscription_plans (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    tier VARCHAR(50) NOT NULL,
    price_monthly NUMERIC(10,2) NOT NULL DEFAULT 0.00,
    price_annual NUMERIC(10,2) NOT NULL DEFAULT 0.00,
    max_products INT NOT NULL DEFAULT 50,
    max_users INT NOT NULL DEFAULT 3,
    max_orders_per_month INT NOT NULL DEFAULT 500,
    features JSONB,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);

-- 2. Enhance tenants table with business details, owner, status, MRR, and plan reference
ALTER TABLE tenants ADD COLUMN IF NOT EXISTS business_name VARCHAR(255);
ALTER TABLE tenants ADD COLUMN IF NOT EXISTS owner_name VARCHAR(255);
ALTER TABLE tenants ADD COLUMN IF NOT EXISTS status VARCHAR(50) DEFAULT 'ACTIVE';
ALTER TABLE tenants ADD COLUMN IF NOT EXISTS mrr NUMERIC(10,2) DEFAULT 0.00;
ALTER TABLE tenants ADD COLUMN IF NOT EXISTS subscription_plan_id UUID;

-- 3. Enhance users table with tenant_id foreign key
ALTER TABLE users ADD COLUMN IF NOT EXISTS tenant_id UUID;

-- 4. Seed standard SaaS subscription plans
INSERT INTO subscription_plans (id, name, tier, price_monthly, price_annual, max_products, max_users, max_orders_per_month, features, is_active)
VALUES
    ('11111111-1111-1111-1111-111111111111', 'Single Mara Chekku Starter', 'STARTER', 1499.00, 14990.00, 15, 2, 300, '["Wood Press Batch Logging", "Basic GST Invoice Generation", "Standard Email Receipts"]', TRUE),
    ('22222222-2222-2222-2222-222222222222', 'Commercial Oil Mill Growth', 'GROWTH', 3999.00, 39990.00, 50, 5, 2500, '["Daily Mandi Seed Price Sync", "Multi-pack Size Matrix (100ml - 15L)", "Multi-carrier Shipping Labels", "Live Inventory Ledger"]', TRUE),
    ('33333333-3333-3333-3333-333333333333', 'Industrial Agro Enterprise', 'ENTERPRISE', 8999.00, 89990.00, 200, 20, 15000, '["Full Multi-tenant Storefront", "Dedicated WhatsApp Business Gateway", "Custom Agro Domain & SSL", "Priority Cold-press Batch Audit"]', TRUE),
    ('44444444-4444-4444-4444-444444444444', 'Mandi Multi-Mill Network', 'MANDI_PRO', 14999.00, 149990.00, 9999, 100, 100000, '["Multi-warehouse Extraction Clusters", "Automated Mandi Spread Arbitrage", "Unlimited Sub-tenant Storefronts", "24/7 Dedicated Support"]', TRUE)
ON CONFLICT (id) DO UPDATE SET
    name = EXCLUDED.name,
    tier = EXCLUDED.tier,
    price_monthly = EXCLUDED.price_monthly,
    price_annual = EXCLUDED.price_annual,
    max_products = EXCLUDED.max_products,
    max_users = EXCLUDED.max_users,
    max_orders_per_month = EXCLUDED.max_orders_per_month,
    features = EXCLUDED.features,
    is_active = TRUE;

-- 5. Add foreign key from tenants to subscription_plans
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'fk_tenants_subscription_plan'
    ) THEN
        ALTER TABLE tenants
        ADD CONSTRAINT fk_tenants_subscription_plan
        FOREIGN KEY (subscription_plan_id) REFERENCES subscription_plans(id) ON DELETE SET NULL;
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'fk_users_tenant'
    ) THEN
        ALTER TABLE users
        ADD CONSTRAINT fk_users_tenant
        FOREIGN KEY (tenant_id) REFERENCES tenants(id) ON DELETE SET NULL;
    END IF;
END $$;

-- 6. Seed / Update tenants with rich mill profiles
INSERT INTO tenants (id, name, slug, business_name, owner_name, email, phone, address, logo, plan, status, mrr, subscription_plan_id, is_active)
VALUES
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Nisha Pure Oils', 'nisha-pure-oils', 'Nisha Traditional Cold Press Agro Foods Pvt Ltd', 'Kaviarasu M', 'admin@nishapureoils.com', '+91 98421 00001', '124, Gandhipuram 4th Street, Coimbatore 641012, Tamil Nadu', 'https://images.unsplash.com/photo-1474979266404-7eaacbcd87c5?w=200&auto=format&fit=crop&q=80', 'ENTERPRISE', 'ACTIVE', 8999.00, '33333333-3333-3333-3333-333333333333', TRUE),
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Varshini Gold', 'varshini-gold', 'Varshini Gold Filtered & Puja Oil Speciality Line', 'Muruganathan S', 'admin@varshinigold.com', '+91 98421 00004', 'Plot 45, SIPCOT Agro Industrial Complex, Kangeyam 638701, Tamil Nadu', 'https://images.unsplash.com/photo-1577937927133-66ef06acdf18?w=200&auto=format&fit=crop&q=80', 'GROWTH', 'ACTIVE', 3999.00, '22222222-2222-2222-2222-222222222222', TRUE)
ON CONFLICT (slug) DO UPDATE SET
    business_name = EXCLUDED.business_name,
    owner_name = EXCLUDED.owner_name,
    email = EXCLUDED.email,
    phone = EXCLUDED.phone,
    address = EXCLUDED.address,
    plan = EXCLUDED.plan,
    status = EXCLUDED.status,
    mrr = EXCLUDED.mrr,
    subscription_plan_id = EXCLUDED.subscription_plan_id,
    is_active = TRUE;

-- 7. Link staff users to their respective tenants
UPDATE users SET tenant_id = 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa' WHERE email IN ('admin@nishapureoils.com', 'warehouse@pureoils.com', 'orders@pureoils.com', 'accounts@pureoils.com', 'support@pureoils.com');
UPDATE users SET tenant_id = 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb' WHERE email = 'admin@varshinigold.com';
