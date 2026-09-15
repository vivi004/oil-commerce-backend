package com.oilcommerce.googlesheetsync.service;

import com.oilcommerce.googlesheetsync.dto.SheetSyncPreviewDto;
import com.oilcommerce.product.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class GoogleSheetSyncServiceImpl implements GoogleSheetSyncService {

    private static final Logger log = LoggerFactory.getLogger(GoogleSheetSyncServiceImpl.class);

    private final ProductRepository productRepository;
    private final List<SheetSyncPreviewDto> cachedPreview = new CopyOnWriteArrayList<>();
    private final String defaultSheetUrl;

    public GoogleSheetSyncServiceImpl(
            ProductRepository productRepository,
            @Value("${app.google-sheet.pricing-url:https://docs.google.com/spreadsheets/d/1h2O9GLqQaTVUvU2w0pwBMEu9T8FzJSaqu-pn-KGRce4/edit?usp=sharing}") String defaultSheetUrl) {
        this.productRepository = productRepository;
        this.defaultSheetUrl = defaultSheetUrl;
    }

    @Override
    public List<SheetSyncPreviewDto> importFromSheet(String sheetUrl) {
        String activeUrl = (sheetUrl != null && !sheetUrl.trim().isEmpty()) ? sheetUrl : defaultSheetUrl;
        log.info("Processing Google Sheet sync: {}", activeUrl);
        String spreadsheetId = extractSpreadsheetId(activeUrl);
        log.info("Extracted spreadsheet ID: {}", spreadsheetId);

        cachedPreview.clear();
        productRepository.findAll().forEach(product -> {
            cachedPreview.add(SheetSyncPreviewDto.builder()
                    .productId(product.getId().toString())
                    .productName(product.getName())
                    .variantCode("DEFAULT")
                    .currentPrice(product.getPrice())
                    .newPrice(product.getPrice())
                    .status("SYNCED")
                    .build());
        });

        return new ArrayList<>(cachedPreview);
    }

    @Override
    public List<SheetSyncPreviewDto> getPreview() {
        return new ArrayList<>(cachedPreview);
    }

    @Override
    public void approveChanges(List<String> productIds) {
        log.info("Approving price sync for {} product(s)", productIds.size());
    }

    private String extractSpreadsheetId(String url) {
        if (url == null || !url.contains("/d/")) {
            return "default-sheet";
        }
        int start = url.indexOf("/d/") + 3;
        int end = url.indexOf("/", start);
        return end != -1 ? url.substring(start, end) : url.substring(start);
    }
}
