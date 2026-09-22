-- V29: Ensure Edible Oil category & Rosi Gold brand are seeded, and update existing Edible Oil products

-- 1. Insert Edible Oil category if not present
INSERT INTO categories (id, name, slug, description, icon, sort_order, active) VALUES
    (gen_random_uuid(), 'Edible Oil', 'edible-oil',
     'Premium multi-seed traditional cold-pressed edible cooking oils.',
     '🛢️', 12, TRUE)
ON CONFLICT (slug) DO UPDATE
    SET name = EXCLUDED.name,
        active = TRUE;

-- 2. Insert Rosi Gold brand if not present
INSERT INTO brands (id, name, slug, description, tagline, origin, active) VALUES
    (gen_random_uuid(), 'Rosi Gold', 'rosi-gold',
     'Pure wood-pressed cooking oils and healthy kitchen commodities.',
     'Goodness of Tradition', 'Kangeyam, Tamil Nadu', TRUE)
ON CONFLICT (slug) DO UPDATE
    SET name = EXCLUDED.name,
        active = TRUE;

-- 3. Fix existing Edible Oil product category link
UPDATE products
SET category_id = (SELECT id FROM categories WHERE slug = 'edible-oil' LIMIT 1)
WHERE (LOWER(name) LIKE '%edible oil%' OR LOWER(sku) = 'vg-eo')
  AND (SELECT id FROM categories WHERE slug = 'edible-oil' LIMIT 1) IS NOT NULL;
