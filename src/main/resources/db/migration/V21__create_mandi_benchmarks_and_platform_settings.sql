-- V21: Create mandi_benchmarks and platform_settings tables and seed commodity benchmarks

-- 1. Create mandi_benchmarks table
CREATE TABLE IF NOT EXISTS mandi_benchmarks (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    commodity VARCHAR(255) NOT NULL,
    hsn_code VARCHAR(50) NOT NULL,
    crop_season VARCHAR(100),
    mandi_benchmark_rate NUMERIC(10,2) NOT NULL,
    edible_classification VARCHAR(100) NOT NULL,
    gst_bracket NUMERIC(5,2) NOT NULL DEFAULT 5.00,
    effective_date VARCHAR(100) NOT NULL DEFAULT 'Today',
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);

-- 2. Create platform_settings table
CREATE TABLE IF NOT EXISTS platform_settings (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    edible_oil_gst NUMERIC(5,2) NOT NULL DEFAULT 5.00,
    lamp_oil_gst NUMERIC(5,2) NOT NULL DEFAULT 18.00,
    mandi_sheet_url TEXT NOT NULL DEFAULT 'https://docs.google.com/spreadsheets/d/1XyZ-SeedMarket-DailyPriceSync-2026/feed',
    auto_sync_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);

-- 3. Seed daily Mandi seed benchmark prices
INSERT INTO mandi_benchmarks (id, commodity, hsn_code, crop_season, mandi_benchmark_rate, edible_classification, gst_bracket, effective_date, status)
VALUES
    (gen_random_uuid(), 'Raw Bold Groundnut Pods (Kadiri-6)', '1202', 'Kharif', 7200.00, 'Edible Oilseed', 5.00, 'Today', 'ACTIVE'),
    (gen_random_uuid(), 'Black Sesame Seeds (Til / Gingelly)', '1207', 'Rabi', 14800.00, 'Edible Oilseed', 5.00, 'Today', 'ACTIVE'),
    (gen_random_uuid(), 'Sun-Dried Copra Halves (Pollachi Grade)', '1203', 'Perennial', 11200.00, 'Edible Oilseed', 5.00, 'Today', 'ACTIVE'),
    (gen_random_uuid(), 'Black Mustard Seeds (Rai / Sarson)', '1205', 'Rabi', 5900.00, 'Edible Oilseed', 5.00, 'Yesterday', 'ACTIVE'),
    (gen_random_uuid(), 'Castor Oil Seeds (Vilakkennai)', '1207', 'Kharif', 6400.00, 'Non-Edible / Pooja', 18.00, '3 days ago', 'ACTIVE'),
    (gen_random_uuid(), 'Mahua Dry Flowers & Kernels (Iluppai)', '1207', 'Forest Gather', 4800.00, 'Non-Edible / Pooja', 18.00, '1 week ago', 'ACTIVE'),
    (gen_random_uuid(), 'Neem Kernel Seeds (Azadirachta Indica)', '1207', 'Summer', 3600.00, 'Non-Edible / Pooja', 18.00, '1 week ago', 'ACTIVE');

-- 4. Seed default global platform settings
INSERT INTO platform_settings (id, edible_oil_gst, lamp_oil_gst, mandi_sheet_url, auto_sync_enabled)
VALUES
    ('99999999-9999-9999-9999-999999999999', 5.00, 18.00, 'https://docs.google.com/spreadsheets/d/1XyZ-SeedMarket-DailyPriceSync-2026/feed', TRUE)
ON CONFLICT (id) DO NOTHING;
