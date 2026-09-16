-- V22: Seed initial inventory balances and stock movement logs for all product variants
DO $$
DECLARE
    v RECORD;
BEGIN
    FOR v IN (SELECT id, product_id, stock_quantity FROM product_variants) LOOP
        -- Seed inventory record
        INSERT INTO inventory (product_id, variant_id, quantity, reserved_quantity, warehouse_location)
        VALUES (v.product_id, v.id, v.stock_quantity, 0, 'Unit #1 Mara Chekku Shed');

        -- Seed initial stock movement log
        INSERT INTO stock_movements (
            product_id, variant_id, movement_type, quantity, previous_quantity, new_quantity, reason, reference_id
        ) VALUES (
            v.product_id, v.id, 'STOCK_IN', v.stock_quantity, 0, v.stock_quantity,
            'Initial extraction batch pressing & packaging', 'BATCH-INIT-2026'
        );
    END LOOP;
END $$;
