package com.oilcommerce.cart.dto;
import lombok.Builder; import lombok.Data;
import java.math.BigDecimal; import java.util.List;
@Data @Builder
public class CartDto {
    private List<CartItemDto> items;
    private int itemCount;
    private BigDecimal subtotal;
    private BigDecimal discount;
    private BigDecimal total;
    private String couponCode;
}
