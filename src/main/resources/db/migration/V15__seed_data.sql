-- V15: Seed categories, brands, and default admin user

-- Seed Brands
INSERT INTO brands (id, name, slug, description, tagline, origin, active) VALUES
    (gen_random_uuid(), 'Nisha Pure Oils', 'nisha-pure-oils',
     'Traditional wood-churned oils preserving ancient extraction methods for maximum nutrition and purity.',
     'Pure. Traditional. Nourishing.', 'Kangeyam, Tamil Nadu', TRUE),
    (gen_random_uuid(), 'Varshini Gold', 'varshini-gold',
     'Premium gold-grade oils extracted using modern cold-press technology with guaranteed purity.',
     'Gold Standard in Purity.', 'Kangeyam, Tamil Nadu', TRUE)
ON CONFLICT (slug) DO NOTHING;

-- Seed Categories
INSERT INTO categories (id, name, slug, description, icon, sort_order, active) VALUES
    (gen_random_uuid(), 'Groundnut Oil', 'groundnut-oil',
     'Traditional wood-churned (Marachekku) and filtered groundnut oils packed with heart-healthy MUFA.',
     '🥜', 1, TRUE),
    (gen_random_uuid(), 'Coconut Oil', 'coconut-oil',
     'Sun-dried copra virgin cold-pressed coconut oil for aromatic cooking, skin glow, and hair health.',
     '🥥', 2, TRUE),
    (gen_random_uuid(), 'Sesame Oil (Gingelly)', 'sesame-oil',
     'Cold-pressed gingelly oil rich in calcium and natural antioxidants.',
     '🌾', 3, TRUE),
    (gen_random_uuid(), 'Castor Oil', 'castor-oil',
     'Pure cold-pressed castor oil (Vilakkennai) for laxative, eyebrow growth, and therapeutic massage.',
     '🌿', 4, TRUE),
    (gen_random_uuid(), 'Lamp Oil (Puja Oil)', 'lamp-oil',
     'Sacred Pancha Deepa blend for steady, soot-free pooja lamp lighting.',
     '🪔', 5, TRUE),
    (gen_random_uuid(), 'Neem Oil', 'neem-oil',
     '100% natural organic cold-pressed neem seed oil for plant protection, hair care, and skin remedies.',
     '🍃', 6, TRUE),
    (gen_random_uuid(), 'Mahua Oil (Iluppai)', 'mahua-oil',
     'Traditional forest-sourced Mahua seed oil for body massage and traditional healing.',
     '🌼', 7, TRUE),
    (gen_random_uuid(), 'Palm Oil', 'palm-oil',
     'Natural palm oil rich in vitamins for cooking and cosmetic applications.',
     '🌴', 8, TRUE),
    (gen_random_uuid(), 'Burfi', 'burfi',
     'Traditional handmade sweets crafted with natural oils and jaggery.',
     '🍬', 9, TRUE),
    (gen_random_uuid(), 'Oil Cake', 'oil-cake',
     'Nutrient-rich oil press cake for livestock feed and organic fertilizer applications.',
     '🧱', 10, TRUE)
ON CONFLICT (slug) DO NOTHING;

-- Seed Default Super Admin User (password: Admin@123)
-- BCrypt hash for 'Admin@123' with strength 12
INSERT INTO users (id, first_name, last_name, email, password, role, email_verified, active)
VALUES (
    gen_random_uuid(),
    'Super',
    'Admin',
    'admin@oilcommerce.in',
    '\\\.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi',
    'SUPER_ADMIN',
    TRUE,
    TRUE
)
ON CONFLICT (email) DO NOTHING;

-- Seed coupons
INSERT INTO coupons (id, code, description, type, value, minimum_order_amount, active) VALUES
    (gen_random_uuid(), 'WELCOME10', 'Welcome discount - 10% off first order', 'PERCENT', 10.00, 299.00, TRUE),
    (gen_random_uuid(), 'BULK20', 'Bulk order discount - 20% off on orders above 2000', 'PERCENT', 20.00, 2000.00, TRUE),
    (gen_random_uuid(), 'SAVE100', 'Flat Rs.100 off on orders above 500', 'FIXED', 100.00, 500.00, TRUE)
ON CONFLICT (code) DO NOTHING;
