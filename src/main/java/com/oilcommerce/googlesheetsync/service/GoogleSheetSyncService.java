package com.oilcommerce.googlesheetsync.service;

import com.oilcommerce.googlesheetsync.dto.SheetSyncPreviewDto;
import java.util.List;

public interface GoogleSheetSyncService {
    /** Fetch the Google Sheet CSV and compute price diffs against live DB variant prices. */
    List<SheetSyncPreviewDto> importFromSheet(String sheetUrl);

    /** Return the cached preview from the last importFromSheet() call. */
    List<SheetSyncPreviewDto> getPreview();

    /**
     * Persist approved price changes to the database.
     * @param skus list of variant SKUs whose new prices should be written to the DB
     */
    void approveChanges(List<String> skus);

    /** Return last sync timestamp (epoch millis), or 0 if never synced. */
    long getLastSyncTimestamp();
}
