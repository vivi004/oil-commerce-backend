package com.oilcommerce.googlesheetsync.service;
import com.oilcommerce.googlesheetsync.dto.SheetSyncPreviewDto;
import java.util.List;
public interface GoogleSheetSyncService {
    List<SheetSyncPreviewDto> importFromSheet(String sheetUrl);
    List<SheetSyncPreviewDto> getPreview();
    void approveChanges(List<String> productIds);
}
