-- V28: Seed Sunflower Oil, Edible Oil categories and Roshini Gold / Rosi Gold brands
INSERT INTO categories (id, name, slug, description, icon, sort_order, active) VALUES
    (gen_random_uuid(), 'Sunflower Oil', 'sunflower-oil',
     'Pure cold-pressed and traditional wood-churned sunflower seed oil rich in Vitamin E and natural unsaturated fatty acids.',
     '🌻', 11, TRUE),
    (gen_random_uuid(), 'Edible Oil', 'edible-oil',
     'Premium multi-seed traditional cold-pressed edible cooking oils.',
     '🛢️', 12, TRUE)
ON CONFLICT (slug) DO NOTHING;

INSERT INTO brands (id, name, slug, description, tagline, origin, active) VALUES
    (gen_random_uuid(), 'Roshini Gold', 'roshini-gold',
     'Premium wood-churned pure traditional cooking oils.',
     'Purity in Every Drop', 'Kangeyam, Tamil Nadu', TRUE),
    (gen_random_uuid(), 'Rosi Gold', 'rosi-gold',
     'Pure wood-pressed cooking oils and healthy kitchen commodities.',
     'Goodness of Tradition', 'Kangeyam, Tamil Nadu', TRUE)
ON CONFLICT (slug) DO NOTHING;

