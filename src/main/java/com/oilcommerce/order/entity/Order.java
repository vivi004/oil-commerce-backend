package com.oilcommerce.order.entity;

import com.oilcommerce.common.BaseEntity;
import com.oilcommerce.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Entity @Table(name = "orders")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Order extends BaseEntity {

    @Column(nullable = false, unique = true) private String orderNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false) private User user;

    @Enumerated(EnumType.STRING) @Column(nullable = false)
    @Builder.Default private OrderStatus status = OrderStatus.PENDING;

    @Enumerated(EnumType.STRING) @Column(nullable = false)
    @Builder.Default private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Column private String paymentMethod;
    @Column private String razorpayOrderId;
    @Column private String razorpayPaymentId;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> items;

    @JdbcTypeCode(SqlTypes.JSON) @Column(nullable = false)
    private Map<String,Object> shippingAddress;

    @Column(nullable = false, precision = 10, scale = 2) private BigDecimal subtotal;
    @Column(nullable = false, precision = 10, scale = 2) @Builder.Default private BigDecimal shippingCost = BigDecimal.ZERO;
    @Column(nullable = false, precision = 10, scale = 2) @Builder.Default private BigDecimal taxAmount = BigDecimal.ZERO;
    @Column(nullable = false, precision = 10, scale = 2) @Builder.Default private BigDecimal discountAmount = BigDecimal.ZERO;
    @Column(nullable = false, precision = 10, scale = 2) private BigDecimal totalAmount;

    @Column private String couponCode;
    @Column private String trackingNumber;
    @Column private String carrier;
    @Column private Instant estimatedDelivery;
    @Column private Instant deliveredAt;
    @Column private String cancelReason;
    @Column private String returnReason;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("createdAt DESC")
    private List<OrderStatusHistory> statusHistory;

    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getRazorpayOrderId() { return razorpayOrderId; }
    public void setRazorpayOrderId(String razorpayOrderId) { this.razorpayOrderId = razorpayOrderId; }
    public String getRazorpayPaymentId() { return razorpayPaymentId; }
    public void setRazorpayPaymentId(String razorpayPaymentId) { this.razorpayPaymentId = razorpayPaymentId; }
    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }
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
    public String getCancelReason() { return cancelReason; }
    public void setCancelReason(String cancelReason) { this.cancelReason = cancelReason; }
    public String getReturnReason() { return returnReason; }
    public void setReturnReason(String returnReason) { this.returnReason = returnReason; }
    public List<OrderStatusHistory> getStatusHistory() { return statusHistory; }
    public void setStatusHistory(List<OrderStatusHistory> statusHistory) { this.statusHistory = statusHistory; }

    public static OrderBuilder builder() {
        return new OrderBuilder();
    }

    public static class OrderBuilder {
        private String orderNumber;
        private User user;
        private OrderStatus status = OrderStatus.PENDING;
        private PaymentStatus paymentStatus = PaymentStatus.PENDING;
        private String paymentMethod;
        private String razorpayOrderId;
        private String razorpayPaymentId;
        private List<OrderItem> items;
        private Map<String,Object> shippingAddress;
        private BigDecimal subtotal;
        private BigDecimal shippingCost = BigDecimal.ZERO;
        private BigDecimal taxAmount = BigDecimal.ZERO;
        private BigDecimal discountAmount = BigDecimal.ZERO;
        private BigDecimal totalAmount;
        private String couponCode;
        private String trackingNumber;
        private String carrier;
        private Instant estimatedDelivery;
        private Instant deliveredAt;
        private String cancelReason;
        private String returnReason;
        private List<OrderStatusHistory> statusHistory;

        public OrderBuilder orderNumber(String orderNumber) { this.orderNumber = orderNumber; return this; }
        public OrderBuilder user(User user) { this.user = user; return this; }
        public OrderBuilder status(OrderStatus status) { this.status = status; return this; }
        public OrderBuilder paymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; return this; }
        public OrderBuilder paymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; return this; }
        public OrderBuilder razorpayOrderId(String razorpayOrderId) { this.razorpayOrderId = razorpayOrderId; return this; }
        public OrderBuilder razorpayPaymentId(String razorpayPaymentId) { this.razorpayPaymentId = razorpayPaymentId; return this; }
        public OrderBuilder items(List<OrderItem> items) { this.items = items; return this; }
        public OrderBuilder shippingAddress(Map<String,Object> shippingAddress) { this.shippingAddress = shippingAddress; return this; }
        public OrderBuilder subtotal(BigDecimal subtotal) { this.subtotal = subtotal; return this; }
        public OrderBuilder shippingCost(BigDecimal shippingCost) { this.shippingCost = shippingCost; return this; }
        public OrderBuilder taxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; return this; }
        public OrderBuilder discountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; return this; }
        public OrderBuilder totalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; return this; }
        public OrderBuilder couponCode(String couponCode) { this.couponCode = couponCode; return this; }
        public OrderBuilder trackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; return this; }
        public OrderBuilder carrier(String carrier) { this.carrier = carrier; return this; }
        public OrderBuilder estimatedDelivery(Instant estimatedDelivery) { this.estimatedDelivery = estimatedDelivery; return this; }
        public OrderBuilder deliveredAt(Instant deliveredAt) { this.deliveredAt = deliveredAt; return this; }
        public OrderBuilder cancelReason(String cancelReason) { this.cancelReason = cancelReason; return this; }
        public OrderBuilder returnReason(String returnReason) { this.returnReason = returnReason; return this; }
        public OrderBuilder statusHistory(List<OrderStatusHistory> statusHistory) { this.statusHistory = statusHistory; return this; }

        public Order build() {
            Order o = new Order();
            o.setOrderNumber(this.orderNumber);
            o.setUser(this.user);
            o.setStatus(this.status != null ? this.status : OrderStatus.PENDING);
            o.setPaymentStatus(this.paymentStatus != null ? this.paymentStatus : PaymentStatus.PENDING);
            o.setPaymentMethod(this.paymentMethod);
            o.setRazorpayOrderId(this.razorpayOrderId);
            o.setRazorpayPaymentId(this.razorpayPaymentId);
            o.setItems(this.items);
            o.setShippingAddress(this.shippingAddress);
            o.setSubtotal(this.subtotal);
            o.setShippingCost(this.shippingCost != null ? this.shippingCost : BigDecimal.ZERO);
            o.setTaxAmount(this.taxAmount != null ? this.taxAmount : BigDecimal.ZERO);
            o.setDiscountAmount(this.discountAmount != null ? this.discountAmount : BigDecimal.ZERO);
            o.setTotalAmount(this.totalAmount);
            o.setCouponCode(this.couponCode);
            o.setTrackingNumber(this.trackingNumber);
            o.setCarrier(this.carrier);
            o.setEstimatedDelivery(this.estimatedDelivery);
            o.setDeliveredAt(this.deliveredAt);
            o.setCancelReason(this.cancelReason);
            o.setReturnReason(this.returnReason);
            o.setStatusHistory(this.statusHistory);
            return o;
        }
    }
}
