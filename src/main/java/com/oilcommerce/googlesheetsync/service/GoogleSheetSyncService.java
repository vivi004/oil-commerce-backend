package com.oilcommerce.googlesheetsync.service;

import com.oilcommerce.googlesheetsync.dto.SheetSyncPreviewDto;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface GoogleSheetSyncService {
    /** Fetch the Google Sheet CSV and compute price diffs against live DB variant prices. */
    List<SheetSyncPreviewDto> importFromSheet(String sheetUrl);

    /** Return the cached preview from the last importFromSheet() call. */
    List<SheetSyncPreviewDto> getPreview();

    /**
     * Persist approved price changes to the database.
     * @param skuPrices map of variant SKU to new selling price (sent directly from frontend,
     *                  does NOT depend on the in-memory cachedPreview -- safe across server restarts).
     */
    void approveChanges(Map<String, BigDecimal> skuPrices);

    /** Return last sync timestamp (epoch millis), or 0 if never synced. */
    long getLastSyncTimestamp();
}
