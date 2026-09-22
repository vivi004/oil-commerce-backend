-- V30: Standardize Oil Brands, Categories and Product Classification

DO $$
BEGIN
    -- 1. Consolidate 'nisha-pure-oil' duplicate if both exist
    IF EXISTS (SELECT 1 FROM brands WHERE slug = 'nisha-pure-oil') AND EXISTS (SELECT 1 FROM brands WHERE slug = 'nisha-pure-oils') THEN
        -- Reassign any products from 'nisha-pure-oil' to 'nisha-pure-oils'
        UPDATE products
        SET brand_id = (SELECT id FROM brands WHERE slug = 'nisha-pure-oils' LIMIT 1)
        WHERE brand_id = (SELECT id FROM brands WHERE slug = 'nisha-pure-oil' LIMIT 1);
        -- Delete the old duplicate
        DELETE FROM brands WHERE slug = 'nisha-pure-oil';
    ELSIF EXISTS (SELECT 1 FROM brands WHERE slug = 'nisha-pure-oil') THEN
        UPDATE brands
        SET name = 'Nisha Pure Oils',
            slug = 'nisha-pure-oils',
            description = '100% Traditional Vaagai Wood Churned & Cold-Pressed Virgin Heritage Oils preserving ancient extraction methods for maximum nutrition and purity.',
            tagline = 'Pure. Traditional. Nourishing.',
            origin = 'Kangeyam, Tamil Nadu',
            active = TRUE,
            deleted = FALSE
        WHERE slug = 'nisha-pure-oil';
    END IF;

    -- Upsert Nisha Pure Oils
    IF NOT EXISTS (SELECT 1 FROM brands WHERE slug = 'nisha-pure-oils') THEN
        IF EXISTS (SELECT 1 FROM brands WHERE LOWER(name) = 'nisha pure oils') THEN
            UPDATE brands
            SET name = 'Nisha Pure Oils',
                slug = 'nisha-pure-oils',
                description = '100% Traditional Vaagai Wood Churned & Cold-Pressed Virgin Heritage Oils preserving ancient extraction methods for maximum nutrition and purity.',
                tagline = 'Pure. Traditional. Nourishing.',
                origin = 'Kangeyam, Tamil Nadu',
                active = TRUE,
                deleted = FALSE
            WHERE LOWER(name) = 'nisha pure oils';
        ELSE
            INSERT INTO brands (id, name, slug, description, tagline, origin, active, deleted)
            VALUES (gen_random_uuid(), 'Nisha Pure Oils', 'nisha-pure-oils',
                    '100% Traditional Vaagai Wood Churned & Cold-Pressed Virgin Heritage Oils preserving ancient extraction methods for maximum nutrition and purity.',
                    'Pure. Traditional. Nourishing.', 'Kangeyam, Tamil Nadu', TRUE, FALSE);
        END IF;
    ELSE
        UPDATE brands
        SET name = 'Nisha Pure Oils',
            description = '100% Traditional Vaagai Wood Churned & Cold-Pressed Virgin Heritage Oils preserving ancient extraction methods for maximum nutrition and purity.',
            tagline = 'Pure. Traditional. Nourishing.',
            origin = 'Kangeyam, Tamil Nadu',
            active = TRUE,
            deleted = FALSE
        WHERE slug = 'nisha-pure-oils';
    END IF;

    -- Upsert Roshini Gold
    IF NOT EXISTS (SELECT 1 FROM brands WHERE slug = 'roshini-gold') THEN
        IF EXISTS (SELECT 1 FROM brands WHERE LOWER(name) = 'roshini gold') THEN
            UPDATE brands
            SET slug = 'roshini-gold',
                description = 'High-Heat Culinary Cooking Label formulated for high heat stability, crisp frying, and everyday wholesome meals.',
                tagline = 'Purity in Every Drop',
                origin = 'Kangeyam, Tamil Nadu',
                active = TRUE,
                deleted = FALSE
            WHERE LOWER(name) = 'roshini gold';
        ELSE
            INSERT INTO brands (id, name, slug, description, tagline, origin, active, deleted)
            VALUES (gen_random_uuid(), 'Roshini Gold', 'roshini-gold',
                    'High-Heat Culinary Cooking Label formulated for high heat stability, crisp frying, and everyday wholesome meals.',
                    'Purity in Every Drop', 'Kangeyam, Tamil Nadu', TRUE, FALSE);
        END IF;
    ELSE
        UPDATE brands
        SET name = 'Roshini Gold',
            description = 'High-Heat Culinary Cooking Label formulated for high heat stability, crisp frying, and everyday wholesome meals.',
            tagline = 'Purity in Every Drop',
            origin = 'Kangeyam, Tamil Nadu',
            active = TRUE,
            deleted = FALSE
        WHERE slug = 'roshini-gold';
    END IF;

    -- Upsert Rosi Gold
    IF NOT EXISTS (SELECT 1 FROM brands WHERE slug = 'rosi-gold') THEN
        IF EXISTS (SELECT 1 FROM brands WHERE LOWER(name) = 'rosi gold') THEN
            UPDATE brands
            SET slug = 'rosi-gold',
                description = 'Wholesome Kitchen & Frying Commodities Label delivering premium culinary palm olein and multi-purpose oils.',
                tagline = 'Goodness of Tradition',
                origin = 'Kangeyam, Tamil Nadu',
                active = TRUE,
                deleted = FALSE
            WHERE LOWER(name) = 'rosi gold';
        ELSE
            INSERT INTO brands (id, name, slug, description, tagline, origin, active, deleted)
            VALUES (gen_random_uuid(), 'Rosi Gold', 'rosi-gold',
                    'Wholesome Kitchen & Frying Commodities Label delivering premium culinary palm olein and multi-purpose oils.',
                    'Goodness of Tradition', 'Kangeyam, Tamil Nadu', TRUE, FALSE);
        END IF;
    ELSE
        UPDATE brands
        SET name = 'Rosi Gold',
            description = 'Wholesome Kitchen & Frying Commodities Label delivering premium culinary palm olein and multi-purpose oils.',
            tagline = 'Goodness of Tradition',
            origin = 'Kangeyam, Tamil Nadu',
            active = TRUE,
            deleted = FALSE
        WHERE slug = 'rosi-gold';
    END IF;

    -- Upsert Varshini Gold
    IF NOT EXISTS (SELECT 1 FROM brands WHERE slug = 'varshini-gold') THEN
        IF EXISTS (SELECT 1 FROM brands WHERE LOWER(name) = 'varshini gold') THEN
            UPDATE brands
            SET slug = 'varshini-gold',
                description = 'Premium Multi-Seed Culinary & Gold Standard Blends with high smoke point and zero cholesterol.',
                tagline = 'Gold Standard in Purity',
                origin = 'Kangeyam, Tamil Nadu',
                active = TRUE,
                deleted = FALSE
            WHERE LOWER(name) = 'varshini gold';
        ELSE
            INSERT INTO brands (id, name, slug, description, tagline, origin, active, deleted)
            VALUES (gen_random_uuid(), 'Varshini Gold', 'varshini-gold',
                    'Premium Multi-Seed Culinary & Gold Standard Blends with high smoke point and zero cholesterol.',
                    'Gold Standard in Purity', 'Kangeyam, Tamil Nadu', TRUE, FALSE);
        END IF;
    ELSE
        UPDATE brands
        SET name = 'Varshini Gold',
            description = 'Premium Multi-Seed Culinary & Gold Standard Blends with high smoke point and zero cholesterol.',
            tagline = 'Gold Standard in Purity',
            origin = 'Kangeyam, Tamil Nadu',
            active = TRUE,
            deleted = FALSE
        WHERE slug = 'varshini-gold';
    END IF;

    -- 2. Update Edible Oil Category Icon
    UPDATE categories
    SET icon = '🫗',
        description = 'Premium multi-seed traditional cold-pressed edible cooking oils.',
        active = TRUE
    WHERE slug = 'edible-oil';

END $$;

-- 3. Update products with their exact Brand, Category, and Extraction Method
DO $$
DECLARE
    brand_nisha_id UUID;
    brand_roshini_id UUID;
    brand_rosi_id UUID;
    brand_varshini_id UUID;

    cat_groundnut_id UUID;
    cat_coconut_id UUID;
    cat_sesame_id UUID;
    cat_castor_id UUID;
    cat_neem_id UUID;
    cat_mahua_id UUID;
    cat_lamp_id UUID;
    cat_sunflower_id UUID;
    cat_palm_id UUID;
    cat_edible_id UUID;
BEGIN
    SELECT id INTO brand_nisha_id FROM brands WHERE slug IN ('nisha-pure-oils', 'nisha-pure-oil') OR LOWER(name) = 'nisha pure oils' ORDER BY created_at ASC LIMIT 1;
    SELECT id INTO brand_roshini_id FROM brands WHERE slug = 'roshini-gold' OR LOWER(name) = 'roshini gold' ORDER BY created_at ASC LIMIT 1;
    SELECT id INTO brand_rosi_id FROM brands WHERE slug = 'rosi-gold' OR LOWER(name) = 'rosi gold' ORDER BY created_at ASC LIMIT 1;
    SELECT id INTO brand_varshini_id FROM brands WHERE slug = 'varshini-gold' OR LOWER(name) = 'varshini gold' ORDER BY created_at ASC LIMIT 1;

    SELECT id INTO cat_groundnut_id FROM categories WHERE slug = 'groundnut-oil' LIMIT 1;
    SELECT id INTO cat_coconut_id FROM categories WHERE slug = 'coconut-oil' LIMIT 1;
    SELECT id INTO cat_sesame_id FROM categories WHERE slug = 'sesame-oil' LIMIT 1;
    SELECT id INTO cat_castor_id FROM categories WHERE slug = 'castor-oil' LIMIT 1;
    SELECT id INTO cat_neem_id FROM categories WHERE slug = 'neem-oil' LIMIT 1;
    SELECT id INTO cat_mahua_id FROM categories WHERE slug = 'mahua-oil' LIMIT 1;
    SELECT id INTO cat_lamp_id FROM categories WHERE slug = 'lamp-oil' LIMIT 1;
    SELECT id INTO cat_sunflower_id FROM categories WHERE slug = 'sunflower-oil' LIMIT 1;
    SELECT id INTO cat_palm_id FROM categories WHERE slug = 'palm-oil' LIMIT 1;
    SELECT id INTO cat_edible_id FROM categories WHERE slug = 'edible-oil' LIMIT 1;

    -- 1. Groundnut Oil: Extraction = Wood-Churned Cold-Pressed, Category = Groundnut Oil, Brand = Nisha Pure Oils
    UPDATE products
    SET brand_id = brand_nisha_id,
        category_id = cat_groundnut_id,
        extraction_method = 'Wood-Churned Cold-Pressed'
    WHERE LOWER(sku) LIKE 'npo-gno%' OR LOWER(name) LIKE '%groundnut%';

    -- 2. Coconut Oil: Extraction = Cold-Pressed Virgin Copra, Category = Coconut Oil, Brand = Nisha Pure Oils
    UPDATE products
    SET brand_id = brand_nisha_id,
        category_id = cat_coconut_id,
        extraction_method = 'Cold-Pressed Virgin Copra'
    WHERE LOWER(sku) LIKE 'npo-cco%' OR LOWER(sku) LIKE 'npo-vco%' OR LOWER(name) LIKE '%coconut%';

    -- 3. Sesame Oil: Extraction = Wood-Churned with Palm Jaggery, Category = Sesame Oil (Gingelly), Brand = Nisha Pure Oils
    UPDATE products
    SET brand_id = brand_nisha_id,
        category_id = cat_sesame_id,
        extraction_method = 'Wood-Churned with Palm Jaggery'
    WHERE LOWER(sku) LIKE 'npo-sso%' OR LOWER(sku) LIKE 'npo-ses%' OR LOWER(name) LIKE '%sesame%' OR LOWER(name) LIKE '%gingelly%';

    -- 4. Castor Oil: Extraction = Pure Cold-Pressed Ricinus, Category = Castor Oil, Brand = Nisha Pure Oils
    UPDATE products
    SET brand_id = brand_nisha_id,
        category_id = cat_castor_id,
        extraction_method = 'Pure Cold-Pressed Ricinus'
    WHERE LOWER(sku) LIKE 'npo-co%' OR LOWER(sku) LIKE 'npo-cas%' OR LOWER(name) LIKE '%castor%';

    -- 5. Neem Oil: Extraction = Organic Cold-Pressed Neem Seed, Category = Neem Oil, Brand = Nisha Pure Oils
    UPDATE products
    SET brand_id = brand_nisha_id,
        category_id = cat_neem_id,
        extraction_method = 'Organic Cold-Pressed Neem Seed'
    WHERE LOWER(sku) LIKE 'npo-no%' OR LOWER(name) LIKE '%neem%';

    -- 6. Mahua Oil: Extraction = Forest-Sourced Iluppai Press, Category = Mahua Oil (Iluppai), Brand = Nisha Pure Oils
    UPDATE products
    SET brand_id = brand_nisha_id,
        category_id = cat_mahua_id,
        extraction_method = 'Forest-Sourced Iluppai Press'
    WHERE LOWER(sku) LIKE 'npo-mo%' OR LOWER(name) LIKE '%mahua%';

    -- 7. Lamp Oil: Extraction = Pancha Deepam 5-Oil Blend, Category = Lamp Oil (Puja Oil), Brand = Nisha Pure Oils
    UPDATE products
    SET brand_id = brand_nisha_id,
        category_id = cat_lamp_id,
        extraction_method = 'Pancha Deepam 5-Oil Blend'
    WHERE LOWER(sku) LIKE 'npo-lo%' OR LOWER(name) LIKE '%lamp%' OR LOWER(name) LIKE '%deepa%';

    -- 8. Sunflower Oil: Extraction = Refined High-Smoke Point, Category = Sunflower Oil, Brand = Roshini Gold
    UPDATE products
    SET brand_id = brand_roshini_id,
        category_id = cat_sunflower_id,
        extraction_method = 'Refined High-Smoke Point'
    WHERE LOWER(sku) LIKE 'ro-so%' OR LOWER(name) LIKE '%sunflower%';

    -- 9. Palm Oil: Extraction = Refined Culinary Palm Olein, Category = Palm Oil, Brand = Rosi Gold
    UPDATE products
    SET brand_id = brand_rosi_id,
        category_id = cat_palm_id,
        extraction_method = 'Refined Culinary Palm Olein'
    WHERE LOWER(sku) LIKE 'rg-po%' OR LOWER(name) LIKE '%palm%';

    -- 10. Edible Oil: Extraction = Traditional Multi-Seed Blend, Category = Edible Oil, Brand = Varshini Gold
    UPDATE products
    SET brand_id = brand_varshini_id,
        category_id = cat_edible_id,
        extraction_method = 'Traditional Multi-Seed Blend'
    WHERE LOWER(sku) LIKE 'vg-eo%' OR LOWER(name) = 'edible oil';
END $$;
