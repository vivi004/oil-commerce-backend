package com.oilcommerce.common;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class PaginatedResponse<T> {
    private final List<T> items;
    private final long total;
    private final int page;
    private final int pageSize;
    private final int totalPages;
    private final boolean hasNextPage;
    private final boolean hasPreviousPage;

    public static <T> PaginatedResponse<T> of(List<T> items, long total, int page, int pageSize) {
        int totalPages = (int) Math.ceil((double) total / pageSize);
        return PaginatedResponse.<T>builder()
                .items(items)
                .total(total)
                .page(page)
                .pageSize(pageSize)
                .totalPages(totalPages)
                .hasNextPage(page < totalPages)
                .hasPreviousPage(page > 1)
                .build();
    }
}
