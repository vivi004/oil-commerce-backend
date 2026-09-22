-- V28: Seed Sunflower Oil category and Roshini Gold brand if not present
INSERT INTO categories (id, name, slug, description, icon, sort_order, active) VALUES
    (gen_random_uuid(), 'Sunflower Oil', 'sunflower-oil',
     'Pure cold-pressed and traditional wood-churned sunflower seed oil rich in Vitamin E and natural unsaturated fatty acids.',
     '🌻', 11, TRUE)
ON CONFLICT (slug) DO NOTHING;

INSERT INTO brands (id, name, slug, description, tagline, origin, active) VALUES
    (gen_random_uuid(), 'Roshini Gold', 'roshini-gold',
     'Premium wood-churned pure traditional cooking oils.',
     'Purity in Every Drop', 'Kangeyam, Tamil Nadu', TRUE)
ON CONFLICT (slug) DO NOTHING;
