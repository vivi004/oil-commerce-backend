package com.oilcommerce.order.dto;
import com.oilcommerce.order.entity.OrderStatus;
import com.oilcommerce.order.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    private UUID id;
    private String orderNumber;
    private UUID userId;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private OrderStatus status;
    private PaymentStatus paymentStatus;
    private String paymentMethod;
    private List<OrderItemDto> items;
    private Map<String,Object> shippingAddress;
    private BigDecimal subtotal;
    private BigDecimal shippingCost;
    private BigDecimal taxAmount;
    private BigDecimal discountAmount;
    private BigDecimal totalAmount;
    private BigDecimal total;
    private String couponCode;
    private String trackingNumber;
    private String carrier;
    private Instant estimatedDelivery;
    private Instant deliveredAt;
    private List<StatusHistoryDto> statusHistory;
    private Instant createdAt;
    private Instant updatedAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public List<OrderItemDto> getItems() { return items; }
    public void setItems(List<OrderItemDto> items) { this.items = items; }
    public Map<String,Object> getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(Map<String,Object> shippingAddress) { this.shippingAddress = shippingAddress; }
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    public BigDecimal getShippingCost() { return shippingCost; }
    public void setShippingCost(BigDecimal shippingCost) { this.shippingCost = shippingCost; }
    public BigDecimal getTaxAmount() { return taxAmount; }
    public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }
    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public String getCouponCode() { return couponCode; }
    public void setCouponCode(String couponCode) { this.couponCode = couponCode; }
    public String getTrackingNumber() { return trackingNumber; }
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }
    public String getCarrier() { return carrier; }
    public void setCarrier(String carrier) { this.carrier = carrier; }
    public Instant getEstimatedDelivery() { return estimatedDelivery; }
    public void setEstimatedDelivery(Instant estimatedDelivery) { this.estimatedDelivery = estimatedDelivery; }
    public Instant getDeliveredAt() { return deliveredAt; }
    public void setDeliveredAt(Instant deliveredAt) { this.deliveredAt = deliveredAt; }
    public List<StatusHistoryDto> getStatusHistory() { return statusHistory; }
    public void setStatusHistory(List<StatusHistoryDto> statusHistory) { this.statusHistory = statusHistory; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public static OrderDtoBuilder builder() {
        return new OrderDtoBuilder();
    }

    public static class OrderDtoBuilder {
        private UUID id;
        private String orderNumber;
        private UUID userId;
        private String customerName;
        private String customerEmail;
        private String customerPhone;
        private OrderStatus status;
        private PaymentStatus paymentStatus;
        private String paymentMethod;
        private List<OrderItemDto> items;
        private Map<String,Object> shippingAddress;
        private BigDecimal subtotal;
        private BigDecimal shippingCost;
        private BigDecimal taxAmount;
        private BigDecimal discountAmount;
        private BigDecimal totalAmount;
        private BigDecimal total;
        private String couponCode;
        private String trackingNumber;
        private String carrier;
        private Instant estimatedDelivery;
        private Instant deliveredAt;
        private List<StatusHistoryDto> statusHistory;
        private Instant createdAt;
        private Instant updatedAt;

        public OrderDtoBuilder id(UUID id) { this.id = id; return this; }
        public OrderDtoBuilder orderNumber(String orderNumber) { this.orderNumber = orderNumber; return this; }
        public OrderDtoBuilder userId(UUID userId) { this.userId = userId; return this; }
        public OrderDtoBuilder customerName(String customerName) { this.customerName = customerName; return this; }
        public OrderDtoBuilder customerEmail(String customerEmail) { this.customerEmail = customerEmail; return this; }
        public OrderDtoBuilder customerPhone(String customerPhone) { this.customerPhone = customerPhone; return this; }
        public OrderDtoBuilder status(OrderStatus status) { this.status = status; return this; }
        public OrderDtoBuilder paymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; return this; }
        public OrderDtoBuilder paymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; return this; }
        public OrderDtoBuilder items(List<OrderItemDto> items) { this.items = items; return this; }
        public OrderDtoBuilder shippingAddress(Map<String,Object> shippingAddress) { this.shippingAddress = shippingAddress; return this; }
        public OrderDtoBuilder subtotal(BigDecimal subtotal) { this.subtotal = subtotal; return this; }
        public OrderDtoBuilder shippingCost(BigDecimal shippingCost) { this.shippingCost = shippingCost; return this; }
        public OrderDtoBuilder taxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; return this; }
        public OrderDtoBuilder discountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; return this; }
        public OrderDtoBuilder totalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; return this; }
        public OrderDtoBuilder total(BigDecimal total) { this.total = total; return this; }
        public OrderDtoBuilder couponCode(String couponCode) { this.couponCode = couponCode; return this; }
        public OrderDtoBuilder trackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; return this; }
        public OrderDtoBuilder carrier(String carrier) { this.carrier = carrier; return this; }
        public OrderDtoBuilder estimatedDelivery(Instant estimatedDelivery) { this.estimatedDelivery = estimatedDelivery; return this; }
        public OrderDtoBuilder deliveredAt(Instant deliveredAt) { this.deliveredAt = deliveredAt; return this; }
        public OrderDtoBuilder statusHistory(List<StatusHistoryDto> statusHistory) { this.statusHistory = statusHistory; return this; }
        public OrderDtoBuilder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public OrderDtoBuilder updatedAt(Instant updatedAt) { this.updatedAt = updatedAt; return this; }

        public OrderDto build() {
            OrderDto dto = new OrderDto();
            dto.setId(this.id);
            dto.setOrderNumber(this.orderNumber);
            dto.setUserId(this.userId);
            dto.setCustomerName(this.customerName);
            dto.setCustomerEmail(this.customerEmail);
            dto.setCustomerPhone(this.customerPhone);
            dto.setStatus(this.status);
            dto.setPaymentStatus(this.paymentStatus);
            dto.setPaymentMethod(this.paymentMethod);
            dto.setItems(this.items);
            dto.setShippingAddress(this.shippingAddress);
            dto.setSubtotal(this.subtotal);
            dto.setShippingCost(this.shippingCost);
            dto.setTaxAmount(this.taxAmount);
            dto.setDiscountAmount(this.discountAmount);
            dto.setTotalAmount(this.totalAmount);
            dto.setTotal(this.total != null ? this.total : this.totalAmount);
            dto.setCouponCode(this.couponCode);
            dto.setTrackingNumber(this.trackingNumber);
            dto.setCarrier(this.carrier);
            dto.setEstimatedDelivery(this.estimatedDelivery);
            dto.setDeliveredAt(this.deliveredAt);
            dto.setStatusHistory(this.statusHistory);
            dto.setCreatedAt(this.createdAt);
            dto.setUpdatedAt(this.updatedAt);
            return dto;
        }
    }
}
