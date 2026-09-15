package com.oilcommerce.product.dto;
import lombok.Data;
import java.math.BigDecimal; import java.util.List;

@Data
public class ProductFilterRequest {
    private String search;
    private String categoryId;
    private List<String> brand;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private BigDecimal minRating;
    private Boolean inStock;
    private Boolean onSale;
    private String sortBy = "newest";
    private int page = 1;
    private int pageSize = 12;
}
