package com.oilcommerce.googlesheetsync.service;

import com.oilcommerce.googlesheetsync.dto.SheetSyncPreviewDto;
import com.oilcommerce.product.entity.ProductVariant;
import com.oilcommerce.product.repository.ProductVariantRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class GoogleSheetSyncServiceImpl implements GoogleSheetSyncService {

    private static final Logger log = LoggerFactory.getLogger(GoogleSheetSyncServiceImpl.class);

    // -------------------------------------------------------------------------
    // Sheet layout constants — Master_Price_Sheet
    //
    // Row 0 (0-indexed): Brand header row  → SKIP
    // Row 1             : Product name row → SKIP (used for column mapping)
    // Rows 2-10         : Size data rows
    //
    // Columns (0-indexed):
    //  Col 0  : Size label (100ml, 200ml, 500ml, 1ltr, 2ltr, 5ltr, 5kg, 15ltr, 15kg)
    //  Col 1  : Groundnut Oil    → NPO-GNO
    //  Col 2  : Coconut Oil      → NPO-COC
    //  Col 3  : Sesame Oil       → NPO-SES
    //  Col 4  : Castor Oil       → NPO-CAS
    //  Col 5  : Lamp Oil         → NPO-LMP
    //  Col 6  : Neem Oil         → NPO-NEM
    //  Col 7  : Mahua Oil        → NPO-MAH
    //  Col 8  : Edible Oil       → VG-EO
    //  Col 9  : Sunflower Oil    → RG-SFO
    //  Col 10 : Palm Oil         → RSG-PO
    // -------------------------------------------------------------------------

    /** col-index → {productName, skuPrefix} */
    private static final Map<Integer, String[]> COL_PRODUCTS = new LinkedHashMap<>();

    /** size label (lowercase) → variant code/size suffix used in SKU */
    private static final Map<String, String> SIZE_TO_CODE = new LinkedHashMap<>();

    static {
        COL_PRODUCTS.put(1,  new String[]{"Groundnut Oil",  "NPO-GNO"});
        COL_PRODUCTS.put(2,  new String[]{"Coconut Oil",    "NPO-COC"});
        COL_PRODUCTS.put(3,  new String[]{"Sesame Oil",     "NPO-SES"});
        COL_PRODUCTS.put(4,  new String[]{"Castor Oil",     "NPO-CAS"});
        COL_PRODUCTS.put(5,  new String[]{"Lamp Oil",       "NPO-LMP"});
        COL_PRODUCTS.put(6,  new String[]{"Neem Oil",       "NPO-NEM"});
        COL_PRODUCTS.put(7,  new String[]{"Mahua Oil",      "NPO-MAH"});
        COL_PRODUCTS.put(8,  new String[]{"Edible Oil",     "VG-EO"});
        COL_PRODUCTS.put(9,  new String[]{"Sunflower Oil",  "RG-SFO"});
        COL_PRODUCTS.put(10, new String[]{"Palm Oil",       "RSG-PO"});
    }

    static {
        // sheet label (lowercase) → variant SKU suffix
        SIZE_TO_CODE.put("100ml",  "100ML");
        SIZE_TO_CODE.put("200ml",  "200ML");
        SIZE_TO_CODE.put("500ml",  "500ML");
        SIZE_TO_CODE.put("1ltr",   "1L");
        SIZE_TO_CODE.put("2ltr",   "2L");
        SIZE_TO_CODE.put("5ltr",   "5L");
        SIZE_TO_CODE.put("5kg",    "5KG");
        SIZE_TO_CODE.put("15ltr",  "15L");
        SIZE_TO_CODE.put("15kg",   "15KG");
    }

    private final ProductVariantRepository variantRepository;
    private final String defaultSheetUrl;
    private final List<SheetSyncPreviewDto> cachedPreview = new CopyOnWriteArrayList<>();
    private final AtomicLong lastSyncTimestamp = new AtomicLong(0);

    public GoogleSheetSyncServiceImpl(
            ProductVariantRepository variantRepository,
            @Value("${app.google-sheet.pricing-url:https://docs.google.com/spreadsheets/d/1-jUkelMl4CmVZnNhmIgHj0shXvG5-3UDZ8s5LRhw0UE/edit?usp=sharing}") String defaultSheetUrl) {
        this.variantRepository = variantRepository;
        this.defaultSheetUrl   = defaultSheetUrl;
    }

    // =========================================================================
    // importFromSheet — fetch CSV, parse, compare with DB
    // =========================================================================
    @Override
    public List<SheetSyncPreviewDto> importFromSheet(String sheetUrl) {
        String activeUrl = (sheetUrl != null && !sheetUrl.trim().isEmpty()) ? sheetUrl.trim() : defaultSheetUrl;
        log.info("Google Sheet sync requested: {}", activeUrl);

        String spreadsheetId = extractSpreadsheetId(activeUrl);
        log.info("Spreadsheet ID: {}", spreadsheetId);

        // Public CSV export URL (no API key required for published / link-shared sheets)
        String csvUrl = "https://docs.google.com/spreadsheets/d/" + spreadsheetId + "/gviz/tq?tqx=out:csv";

        String csvText;
        try {
            csvText = fetchCsv(csvUrl);
        } catch (Exception e) {
            log.error("Failed to fetch Google Sheet CSV from {}: {}", csvUrl, e.getMessage());
            return new ArrayList<>(cachedPreview); // return stale cache rather than crashing
        }

        List<SheetSyncPreviewDto> diffs = parseCsvAndBuildDiffs(csvText);
        cachedPreview.clear();
        cachedPreview.addAll(diffs);
        lastSyncTimestamp.set(System.currentTimeMillis());

        log.info("Sheet sync complete. Found {} price diff(s).", diffs.size());
        return new ArrayList<>(cachedPreview);
    }

    // =========================================================================
    // getPreview — return cached result
    // =========================================================================
    @Override
    public List<SheetSyncPreviewDto> getPreview() {
        return new ArrayList<>(cachedPreview);
    }

    // =========================================================================
    // approveChanges — persist new prices to DB for approved SKUs
    // =========================================================================
    @Override
    @Transactional
    public void approveChanges(List<String> skus) {
        if (skus == null || skus.isEmpty()) {
            log.warn("approveChanges called with empty SKU list");
            return;
        }
        log.info("Approving price sync for {} SKU(s): {}", skus.size(), skus);

        // Build a map of sku → newPrice from the cached preview
        Map<String, BigDecimal> skuToNewPrice = new LinkedHashMap<>();
        for (SheetSyncPreviewDto item : cachedPreview) {
            if (item.getSku() != null && item.getNewPrice() != null) {
                skuToNewPrice.put(item.getSku().trim().toUpperCase(), item.getNewPrice());
            }
        }

        int updated = 0;
        for (String sku : skus) {
            String normalised = sku.trim().toUpperCase();
            BigDecimal newPrice = skuToNewPrice.get(normalised);
            if (newPrice == null) {
                log.warn("No cached new price found for SKU '{}', skipping", sku);
                continue;
            }

            Optional<ProductVariant> opt = variantRepository.findBySkuAndDeletedFalse(sku.trim());
            if (opt.isPresent()) {
                ProductVariant variant = opt.get();
                BigDecimal oldPrice = variant.getSellingPrice();
                variant.setSellingPrice(newPrice);
                variantRepository.save(variant);
                log.info("Updated SKU {} sellingPrice: {} → {}", sku, oldPrice, newPrice);
                updated++;
            } else {
                log.warn("Variant SKU '{}' not found in DB — skipping", sku);
            }
        }

        // Remove approved items from cache
        cachedPreview.removeIf(item -> item.getSku() != null && skus.stream()
                .anyMatch(s -> s.trim().equalsIgnoreCase(item.getSku())));

        log.info("approveChanges: updated {} variant(s) in DB", updated);
    }

    // =========================================================================
    // getLastSyncTimestamp
    // =========================================================================
    @Override
    public long getLastSyncTimestamp() {
        return lastSyncTimestamp.get();
    }

    // =========================================================================
    // Private helpers
    // =========================================================================

    private String fetchCsv(String csvUrl) throws Exception {
        log.info("Fetching CSV from: {}", csvUrl);
        URL url = new URL(csvUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(10_000);
        conn.setReadTimeout(15_000);
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 OilCommerce-SheetSync/1.0");

        int status = conn.getResponseCode();
        if (status != 200) {
            throw new RuntimeException("Sheet fetch returned HTTP " + status);
        }

        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } finally {
            conn.disconnect();
        }
        return sb.toString();
    }

    private List<SheetSyncPreviewDto> parseCsvAndBuildDiffs(String csvText) {
        List<SheetSyncPreviewDto> diffs = new ArrayList<>();
        String[] lines = csvText.split("\r?\n");

        if (lines.length < 3) {
            log.warn("CSV has fewer than 3 lines — cannot parse");
            return diffs;
        }

        // Row 0: brand header → SKIP
        // Row 1: product name header → SKIP (column positions are fixed per our mapping)
        // Rows 2+: size data rows

        for (int rowIdx = 2; rowIdx < lines.length; rowIdx++) {
            String line = lines[rowIdx].trim();
            if (line.isEmpty()) continue;

            String[] cols = parseCsvLine(line);
            if (cols.length < 2) continue;

            // Column 0 = size label
            String sizeLabel = cols[0].trim().toLowerCase();
            String sizeCode  = SIZE_TO_CODE.get(sizeLabel);
            if (sizeCode == null) {
                log.debug("Unknown size label '{}' on row {}, skipping", sizeLabel, rowIdx);
                continue;
            }

            // Columns 1–10 = product prices
            for (Map.Entry<Integer, String[]> entry : COL_PRODUCTS.entrySet()) {
                int colIdx = entry.getKey();
                if (colIdx >= cols.length) continue;

                String[] productMeta = entry.getValue();
                String productName   = productMeta[0];
                String skuPrefix     = productMeta[1];

                String cellValue = cols[colIdx].trim();

                // Empty or dash means not available for this size
                if (cellValue.isEmpty() || cellValue.equals("-")) continue;

                BigDecimal sheetPrice;
                try {
                    // Remove any non-numeric chars except decimal point (e.g. ₹, commas)
                    sheetPrice = new BigDecimal(cellValue.replaceAll("[^0-9.]", ""));
                } catch (NumberFormatException e) {
                    log.debug("Non-numeric cell '{}' at row={} col={}, skipping", cellValue, rowIdx, colIdx);
                    continue;
                }

                // Build expected SKU: e.g. NPO-GNO-1L
                String expectedSku = skuPrefix + "-" + sizeCode;

                // Look up variant in DB
                Optional<ProductVariant> variantOpt = variantRepository.findBySkuAndDeletedFalse(expectedSku);
                if (variantOpt.isEmpty()) {
                    // Try case-insensitive by iterating (in case SKU case differs)
                    log.debug("Variant SKU '{}' not found in DB, skipping", expectedSku);
                    continue;
                }

                ProductVariant variant = variantOpt.get();
                BigDecimal currentPrice = variant.getSellingPrice() != null ? variant.getSellingPrice() : BigDecimal.ZERO;
                BigDecimal delta        = sheetPrice.subtract(currentPrice);

                // Only include in diff if price actually changed
                if (delta.compareTo(BigDecimal.ZERO) == 0) {
                    continue;
                }

                BigDecimal mrp = variant.getMrp() != null ? variant.getMrp() : BigDecimal.ZERO;

                SheetSyncPreviewDto dto = SheetSyncPreviewDto.builder()
                        .productId(variant.getProduct() != null ? variant.getProduct().getId().toString() : "")
                        .productName(productName)
                        .sku(expectedSku)
                        .variantSize(sizeCode)
                        .currentPrice(currentPrice)
                        .mrp(mrp)
                        .newMrp(mrp)     // Sheet has no MRP column — keep existing MRP
                        .currentStock(variant.getStockQuantity())
                        .newPrice(sheetPrice)
                        .priceDelta(delta)
                        .status("PENDING")
                        .approved(true)  // default approved so admin can un-check
                        .build();

                diffs.add(dto);
                log.debug("Diff found: {} {} {} → {}", expectedSku, sizeLabel, currentPrice, sheetPrice);
            }
        }

        return diffs;
    }

    /**
     * Parse a single CSV line, handling quoted fields with embedded commas.
     */
    private String[] parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"'); // escaped quote
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                fields.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        fields.add(current.toString());
        return fields.toArray(String[]::new);
    }

    private String extractSpreadsheetId(String url) {
        if (url == null || !url.contains("/d/")) return "default-sheet";
        int start = url.indexOf("/d/") + 3;
        int end   = url.indexOf("/", start);
        return end != -1 ? url.substring(start, end) : url.substring(start);
    }
}
