-- V17: Seed cold-pressed oil products and variants into PostgreSQL
DO $$
DECLARE
    brand_nisha_id UUID;
    brand_varshini_id UUID;
    cat_groundnut_id UUID;
    cat_coconut_id UUID;
    cat_sesame_id UUID;
    cat_castor_id UUID;
    cat_lamp_id UUID;
    cat_neem_id UUID;
    cat_mahua_id UUID;

    prod_gno_id UUID := gen_random_uuid();
    prod_vco_id UUID := gen_random_uuid();
    prod_ses_id UUID := gen_random_uuid();
    prod_cas_id UUID := gen_random_uuid();
    prod_lmp_id UUID := gen_random_uuid();
BEGIN
    SELECT id INTO brand_nisha_id FROM brands WHERE slug = 'nisha-pure-oils' LIMIT 1;
    SELECT id INTO brand_varshini_id FROM brands WHERE slug = 'varshini-gold' LIMIT 1;

    SELECT id INTO cat_groundnut_id FROM categories WHERE slug = 'groundnut-oil' LIMIT 1;
    SELECT id INTO cat_coconut_id FROM categories WHERE slug = 'coconut-oil' LIMIT 1;
    SELECT id INTO cat_sesame_id FROM categories WHERE slug = 'sesame-oil' LIMIT 1;
    SELECT id INTO cat_castor_id FROM categories WHERE slug = 'castor-oil' LIMIT 1;
    SELECT id INTO cat_lamp_id FROM categories WHERE slug = 'lamp-oil' LIMIT 1;

    -- 1. Wood Pressed Groundnut Oil
    INSERT INTO products (
        id, name, slug, description, short_description, price, compare_at_price,
        sku, barcode, stock, low_stock_threshold, category_id, brand_id,
        thumbnail, rating, review_count, featured, on_sale, best_seller, status,
        origin, shelf_life, purity
    ) VALUES (
        prod_gno_id,
        'Wood Pressed Groundnut Oil (Marachekku Kadalai Ennai)',
        'wood-pressed-groundnut-oil',
        'Extracted using traditional Vaagai wood churners under 40°C. Zero chemical refining, natural golden color with rich peanut aroma.',
        'Traditional wood pressed peanut oil packed with heart-healthy monounsaturated fats.',
        95.00, 110.00, 'NPO-GNO-001', '8901234560011', 345, 25, cat_groundnut_id, brand_nisha_id,
        'https://images.unsplash.com/photo-1474979266404-7eaacbcd87c5?w=600&auto=format&fit=crop&q=80',
        4.9, 142, TRUE, FALSE, TRUE, 'ACTIVE',
        'Kangeyam, Tamil Nadu', '12 Months', '100% Cold-Pressed Unrefined'
    ) ON CONFLICT (sku) DO NOTHING;

    INSERT INTO product_variants (product_id, code, label, mrp, selling_price, sku, barcode, stock_quantity, enabled, gst_percent)
    VALUES
        (prod_gno_id, '200ml', '200 ml Bottle', 110, 95, 'NPO-GNO-200ML', '890123456011', 80, TRUE, 5),
        (prod_gno_id, '500ml', '500 ml Bottle', 210, 185, 'NPO-GNO-500ML', '890123456012', 95, TRUE, 5),
        (prod_gno_id, '1L', '1 Liter Bottle', 390, 340, 'NPO-GNO-1L', '890123456013', 120, TRUE, 5),
        (prod_gno_id, '5L', '5 Liter Can', 1850, 1650, 'NPO-GNO-5L', '890123456014', 40, TRUE, 5),
        (prod_gno_id, '15L', '15 Liter Tin', 4200, 3850, 'NPO-GNO-15L', '890123456015', 10, TRUE, 5)
    ON CONFLICT (sku) DO NOTHING;

    -- 2. Cold Pressed Virgin Coconut Oil
    INSERT INTO products (
        id, name, slug, description, short_description, price, compare_at_price,
        sku, barcode, stock, low_stock_threshold, category_id, brand_id,
        thumbnail, rating, review_count, featured, on_sale, best_seller, status,
        origin, shelf_life, purity
    ) VALUES (
        prod_vco_id,
        'Cold Pressed Virgin Coconut Oil (Thengai Ennai)',
        'cold-pressed-virgin-coconut-oil',
        'Crafted from sulfur-free naturally dried copra. Excellent for authentic South Indian cooking, skin hydration, and baby massage.',
        'Sulfur-free virgin coconut oil rich in Lauric acid and MCTs.',
        130.00, 145.00, 'NPO-VCO-002', '8901234560028', 190, 20, cat_coconut_id, brand_nisha_id,
        'https://images.unsplash.com/photo-1526947425960-945c6e72858f?w=600&auto=format&fit=crop&q=80',
        4.8, 98, TRUE, FALSE, TRUE, 'ACTIVE',
        'Pollachi, Tamil Nadu', '18 Months', '100% Pure Copra Extraction'
    ) ON CONFLICT (sku) DO NOTHING;

    INSERT INTO product_variants (product_id, code, label, mrp, selling_price, sku, barcode, stock_quantity, enabled, gst_percent)
    VALUES
        (prod_vco_id, '200ml', '200 ml Bottle', 145, 130, 'NPO-VCO-200ML', '890123456021', 50, TRUE, 5),
        (prod_vco_id, '500ml', '500 ml Bottle', 280, 250, 'NPO-VCO-500ML', '890123456022', 65, TRUE, 5),
        (prod_vco_id, '1L', '1 Liter Bottle', 520, 470, 'NPO-VCO-1L', '890123456023', 60, TRUE, 5),
        (prod_vco_id, '5L', '5 Liter Can', 2450, 2200, 'NPO-VCO-5L', '890123456024', 15, TRUE, 5)
    ON CONFLICT (sku) DO NOTHING;

    -- 3. Traditional Wood Pressed Sesame Oil
    INSERT INTO products (
        id, name, slug, description, short_description, price, compare_at_price,
        sku, barcode, stock, low_stock_threshold, category_id, brand_id,
        thumbnail, rating, review_count, featured, on_sale, best_seller, status,
        origin, shelf_life, purity
    ) VALUES (
        prod_ses_id,
        'Traditional Wood Pressed Sesame Oil (Gingelly / Nalla Ennai)',
        'wood-pressed-sesame-oil',
        'Black sesame seeds slowly crushed with natural palm jaggery (Karupatti) to eliminate raw bitterness and impart authentic South Indian depth.',
        'Wood pressed gingelly oil blended with organic palm jaggery.',
        140.00, 160.00, 'NPO-SES-003', '8901234560035', 215, 20, cat_sesame_id, brand_nisha_id,
        'https://images.unsplash.com/photo-1615485290382-441e4d049cb5?w=600&auto=format&fit=crop&q=80',
        4.9, 116, TRUE, TRUE, FALSE, 'ACTIVE',
        'Madurai, Tamil Nadu', '12 Months', '90% Black Sesame + 10% Palm Jaggery'
    ) ON CONFLICT (sku) DO NOTHING;

    INSERT INTO product_variants (product_id, code, label, mrp, selling_price, sku, barcode, stock_quantity, enabled, gst_percent)
    VALUES
        (prod_ses_id, '200ml', '200 ml Bottle', 160, 140, 'NPO-SES-200ML', '890123456031', 45, TRUE, 5),
        (prod_ses_id, '500ml', '500 ml Bottle', 310, 275, 'NPO-SES-500ML', '890123456032', 70, TRUE, 5),
        (prod_ses_id, '1L', '1 Liter Bottle', 560, 490, 'NPO-SES-1L', '890123456033', 85, TRUE, 5),
        (prod_ses_id, '5L', '5 Liter Can', 2600, 2350, 'NPO-SES-5L', '890123456034', 15, TRUE, 5)
    ON CONFLICT (sku) DO NOTHING;

    -- 4. Cold Pressed Castor Oil
    INSERT INTO products (
        id, name, slug, description, short_description, price, compare_at_price,
        sku, barcode, stock, low_stock_threshold, category_id, brand_id,
        thumbnail, rating, review_count, featured, on_sale, best_seller, status,
        origin, shelf_life, purity
    ) VALUES (
        prod_cas_id,
        'Cold Pressed Pure Castor Oil (Vilakkennai)',
        'cold-pressed-castor-oil',
        'Dense, rich ricinoleic acid profile extracted without solvent hexane. Ideal for body cooling, joint aches, and hair care.',
        'Unrefined thick castor oil for natural wellness and cooling.',
        105.00, 120.00, 'NPO-CAS-004', '8901234560042', 120, 15, cat_castor_id, brand_nisha_id,
        'https://images.unsplash.com/photo-1608571423902-eed4a5ad8108?w=600&auto=format&fit=crop&q=80',
        4.7, 54, FALSE, FALSE, FALSE, 'ACTIVE',
        'Salem, Tamil Nadu', '24 Months', '100% Cold-Pressed Ricinus'
    ) ON CONFLICT (sku) DO NOTHING;

    INSERT INTO product_variants (product_id, code, label, mrp, selling_price, sku, barcode, stock_quantity, enabled, gst_percent)
    VALUES
        (prod_cas_id, '200ml', '200 ml Bottle', 120, 105, 'NPO-CAS-200ML', '890123456041', 40, TRUE, 5),
        (prod_cas_id, '500ml', '500 ml Bottle', 230, 195, 'NPO-CAS-500ML', '890123456042', 50, TRUE, 5),
        (prod_cas_id, '1L', '1 Liter Bottle', 420, 370, 'NPO-CAS-1L', '890123456043', 30, TRUE, 5)
    ON CONFLICT (sku) DO NOTHING;

    -- 5. Pancha Deepam Puja Lamp Oil
    INSERT INTO products (
        id, name, slug, description, short_description, price, compare_at_price,
        sku, barcode, stock, low_stock_threshold, category_id, brand_id,
        thumbnail, rating, review_count, featured, on_sale, best_seller, status,
        origin, shelf_life, purity
    ) VALUES (
        prod_lmp_id,
        'Pancha Deepam Sacred Lamp Oil (Puja Fuel)',
        'pancha-deepam-lamp-oil',
        'A sacred blend of 5 pure oils: Sesame, Mahua, Castor, Neem, and Pure Cow Ghee infused with soothing camphor aroma for long-burning holy lamps.',
        'Divine 5-oil blend for long-lasting, soot-free pooja deepams.',
        125.00, 140.00, 'VG-LMP-005', '8901234560059', 240, 30, cat_lamp_id, brand_varshini_id,
        'https://images.unsplash.com/photo-1577937927133-66ef06acdf18?w=600&auto=format&fit=crop&q=80',
        4.9, 184, TRUE, FALSE, TRUE, 'ACTIVE',
        'Erode, Tamil Nadu', '24 Months', 'Pancha Deepa Authentic Formula'
    ) ON CONFLICT (sku) DO NOTHING;

    INSERT INTO product_variants (product_id, code, label, mrp, selling_price, sku, barcode, stock_quantity, enabled, gst_percent)
    VALUES
        (prod_lmp_id, '500ml', '500 ml Bottle', 140, 125, 'VG-LMP-500ML', '890123456051', 70, TRUE, 18),
        (prod_lmp_id, '1L', '1 Liter Bottle', 260, 230, 'VG-LMP-1L', '890123456052', 110, TRUE, 18),
        (prod_lmp_id, '2L', '2 Liter Jar', 490, 440, 'VG-LMP-2L', '890123456053', 40, TRUE, 18),
        (prod_lmp_id, '5L', '5 Liter Can', 1150, 1020, 'VG-LMP-5L', '890123456054', 20, TRUE, 18)
    ON CONFLICT (sku) DO NOTHING;
END $$;
