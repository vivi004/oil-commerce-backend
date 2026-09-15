package com.oilcommerce.order.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class CreateOrderRequest {
    @NotNull private Map<String,Object> shippingAddress;
    @NotBlank private String paymentMethod;
    private String couponCode;
    private List<OrderItemDto> items;

    public Map<String,Object> getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(Map<String,Object> shippingAddress) { this.shippingAddress = shippingAddress; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getCouponCode() { return couponCode; }
    public void setCouponCode(String couponCode) { this.couponCode = couponCode; }
    public List<OrderItemDto> getItems() { return items; }
    public void setItems(List<OrderItemDto> items) { this.items = items; }
}
