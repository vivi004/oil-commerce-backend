package com.oilcommerce.order.service;

import com.oilcommerce.cart.entity.CartItem;
import com.oilcommerce.cart.repository.CartRepository;
import com.oilcommerce.cart.service.CartService;
import com.oilcommerce.common.PaginatedResponse;
import com.oilcommerce.exception.BusinessException;
import com.oilcommerce.exception.ResourceNotFoundException;
import com.oilcommerce.order.dto.*;
import com.oilcommerce.order.entity.*;
import com.oilcommerce.order.repository.OrderRepository;
import com.oilcommerce.coupon.service.CouponService;
import com.oilcommerce.inventory.service.InventoryService;
import com.oilcommerce.user.entity.User;
import com.oilcommerce.user.repository.UserRepository;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final CartService cartService;
    private final UserRepository userRepository;
    private final InventoryService inventoryService;
    private final CouponService couponService;

    public OrderService(OrderRepository orderRepository, CartRepository cartRepository, CartService cartService, UserRepository userRepository, InventoryService inventoryService, CouponService couponService) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.cartService = cartService;
        this.userRepository = userRepository;
        this.inventoryService = inventoryService;
        this.couponService = couponService;
    }

    @Transactional
    public OrderDto createOrder(UUID userId, CreateOrderRequest req) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User","id",userId));
        List<OrderItem> items;
        BigDecimal subtotal;
        BigDecimal tax;
        BigDecimal total;

        String orderNum = "ORD-" + (1000 + new Random().nextInt(9000)) + "-" + java.time.Year.now().getValue();

        Order order = Order.builder()
            .orderNumber(orderNum).user(user)
            .shippingAddress(req.getShippingAddress())
            .paymentMethod(req.getPaymentMethod())
            .couponCode(req.getCouponCode())
            .shippingCost(BigDecimal.ZERO)
            .estimatedDelivery(Instant.now().plusSeconds(3 * 24 * 60 * 60))
            .build();

        if (req.getItems() != null && !req.getItems().isEmpty()) {
            items = req.getItems().stream().map(it -> OrderItem.builder()
                .order(order)
                .productId(it.getProductId() != null ? it.getProductId() : UUID.randomUUID().toString())
                .productName(it.getProductName() != null ? it.getProductName() : "Pure Oil Item")
                .productImage(it.getProductImage())
                .sku(it.getSku() != null ? it.getSku() : "NPO-SKU")
                .variantId(it.getVariantId())
                .quantity(it.getQuantity() > 0 ? it.getQuantity() : 1)
                .unitPrice(it.getUnitPrice() != null ? it.getUnitPrice() : BigDecimal.ZERO)
                .totalPrice(it.getTotalPrice() != null ? it.getTotalPrice() : (it.getUnitPrice() != null ? it.getUnitPrice().multiply(BigDecimal.valueOf(it.getQuantity() > 0 ? it.getQuantity() : 1)) : BigDecimal.ZERO))
                .build()
            ).collect(Collectors.toList());

            subtotal = items.stream().map(OrderItem::getTotalPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            tax = subtotal.multiply(new BigDecimal("0.05"));
        } else {
            List<CartItem> cartItems = cartRepository.findByUserIdAndDeletedFalse(userId);
            if (cartItems.isEmpty()) throw new BusinessException("Cart is empty");

            subtotal = cartItems.stream().map(CartItem::getTotalPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            tax = subtotal.multiply(new BigDecimal("0.05"));

            items = cartItems.stream().map(ci -> OrderItem.builder()
                .order(order)
                .productId(ci.getProduct().getId().toString())
                .productName(ci.getProduct().getName())
                .productImage(ci.getProduct().getThumbnail())
                .sku(ci.getProduct().getSku())
                .variantId(ci.getVariant() != null ? ci.getVariant().getId().toString() : null)
                .variantCode(ci.getVariant() != null ? ci.getVariant().getCode() : null)
                .quantity(ci.getQuantity()).unitPrice(ci.getUnitPrice()).totalPrice(ci.getTotalPrice())
                .build()
            ).collect(Collectors.toList());

            cartService.clearCart(userId);
        }

        BigDecimal discount = BigDecimal.ZERO;
        if (req.getCouponCode() != null && !req.getCouponCode().isBlank()) {
            try {
                discount = couponService.calculateDiscountForAmount(req.getCouponCode(), subtotal);
                couponService.recordCouponUsage(req.getCouponCode());
            } catch (Exception e) {
                // If invalid coupon, proceed with zero discount
            }
        }

        order.setSubtotal(subtotal);
        order.setTaxAmount(tax);
        order.setDiscountAmount(discount);
        BigDecimal discountedSubtotal = subtotal.subtract(discount).max(BigDecimal.ZERO);
        total = discountedSubtotal.add(tax);
        order.setTotalAmount(total);

        OrderStatusHistory history = OrderStatusHistory.builder()
            .order(order).status(OrderStatus.PENDING).note("Order placed successfully").build();

        order.setItems(items);
        order.setStatusHistory(List.of(history));
        Order saved = orderRepository.save(order);

        // Deduct inventory for all items in order
        if (saved.getItems() != null) {
            for (OrderItem it : saved.getItems()) {
                inventoryService.deductStockForOrderItem(
                    it.getProductId(),
                    it.getVariantId(),
                    it.getSku(),
                    it.getQuantity(),
                    saved.getOrderNumber()
                );
            }
        }

        return toDto(saved);
    }

    public PaginatedResponse<OrderDto> getUserOrders(UUID userId, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC,"createdAt"));
        Page<Order> result = orderRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        return PaginatedResponse.of(result.getContent().stream().map(this::toDto).toList(), result.getTotalElements(), page, pageSize);
    }

    public OrderDto getOrderById(UUID userId, String orderId) {
        Order order;
        try {
            order = orderRepository.findById(UUID.fromString(orderId))
                    .orElseGet(() -> orderRepository.findByOrderNumber(orderId)
                            .orElseThrow(() -> new ResourceNotFoundException("Order","id",orderId)));
        } catch(IllegalArgumentException e) {
            order = orderRepository.findByOrderNumber(orderId)
                    .orElseThrow(() -> new ResourceNotFoundException("Order","orderNumber",orderId));
        }
        return toDto(order);
    }

    @Transactional
    public OrderDto cancelOrder(UUID userId, UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order","id",orderId));
        if (!order.getUser().getId().equals(userId)) throw new BusinessException("Order not found", HttpStatus.NOT_FOUND);
        if (order.getStatus() == OrderStatus.SHIPPED || order.getStatus() == OrderStatus.DELIVERED)
            throw new BusinessException("Cannot cancel shipped or delivered order");
        order.setStatus(OrderStatus.CANCELLED);

        // Restore stock
        if (order.getItems() != null) {
            for (OrderItem it : order.getItems()) {
                inventoryService.restoreStockForOrderItem(
                    it.getProductId(),
                    it.getVariantId(),
                    it.getSku(),
                    it.getQuantity(),
                    order.getOrderNumber(),
                    "Cancelled by customer"
                );
            }
        }

        OrderStatusHistory h = OrderStatusHistory.builder().order(order).status(OrderStatus.CANCELLED).note("Cancelled by customer").build();
        order.getStatusHistory().add(h);
        return toDto(orderRepository.save(order));
    }

    @Transactional
    public OrderDto returnOrder(UUID userId, UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order","id",orderId));
        if (!order.getUser().getId().equals(userId)) throw new BusinessException("Order not found", HttpStatus.NOT_FOUND);
        if (order.getStatus() != OrderStatus.DELIVERED) throw new BusinessException("Only delivered orders can be returned");
        order.setStatus(OrderStatus.RETURNED);

        // Restore stock
        if (order.getItems() != null) {
            for (OrderItem it : order.getItems()) {
                inventoryService.restoreStockForOrderItem(
                    it.getProductId(),
                    it.getVariantId(),
                    it.getSku(),
                    it.getQuantity(),
                    order.getOrderNumber(),
                    "Return requested by customer"
                );
            }
        }

        OrderStatusHistory h = OrderStatusHistory.builder().order(order).status(OrderStatus.RETURNED).note("Return requested by customer").build();
        order.getStatusHistory().add(h);
        return toDto(orderRepository.save(order));
    }

    public List<StatusHistoryDto> getTracking(String orderIdOrNumber) {
        Order order;
        try {
            order = orderRepository.findById(UUID.fromString(orderIdOrNumber))
                    .orElseGet(() -> orderRepository.findByOrderNumber(orderIdOrNumber)
                            .orElseThrow(() -> new ResourceNotFoundException("Order","id",orderIdOrNumber)));
        } catch(IllegalArgumentException e) {
            order = orderRepository.findByOrderNumber(orderIdOrNumber)
                    .orElseThrow(() -> new ResourceNotFoundException("Order","orderNumber",orderIdOrNumber));
        }
        return order.getStatusHistory().stream().map(h ->
            StatusHistoryDto.builder().status(h.getStatus()).timestamp(h.getCreatedAt()).note(h.getNote()).build()
        ).toList();
    }

    // Admin
    @Transactional
    public OrderDto updateOrderStatus(String orderIdOrNumber, UpdateOrderStatusRequest req) {
        Order order;
        try {
            order = orderRepository.findById(UUID.fromString(orderIdOrNumber))
                    .orElseGet(() -> orderRepository.findByOrderNumber(orderIdOrNumber)
                            .orElseThrow(() -> new ResourceNotFoundException("Order","id",orderIdOrNumber)));
        } catch(IllegalArgumentException e) {
            order = orderRepository.findByOrderNumber(orderIdOrNumber)
                    .orElseThrow(() -> new ResourceNotFoundException("Order","orderNumber",orderIdOrNumber));
        }
        OrderStatus oldStatus = order.getStatus();
        OrderStatus status;
        try {
            status = OrderStatus.valueOf(req.getStatus().trim().toUpperCase());
        } catch(Exception e) {
            status = OrderStatus.CONFIRMED;
        }
        order.setStatus(status);

        // If status changed to CANCELLED or RETURNED, restore stock
        if ((status == OrderStatus.CANCELLED || status == OrderStatus.RETURNED) &&
            oldStatus != OrderStatus.CANCELLED && oldStatus != OrderStatus.RETURNED) {
            if (order.getItems() != null) {
                for (OrderItem it : order.getItems()) {
                    inventoryService.restoreStockForOrderItem(
                        it.getProductId(),
                        it.getVariantId(),
                        it.getSku(),
                        it.getQuantity(),
                        order.getOrderNumber(),
                        "Order " + status.name().toLowerCase() + " by admin"
                    );
                }
            }
        }

        if (status == OrderStatus.DELIVERED) {
            order.setDeliveredAt(java.time.Instant.now());
        }
        if (req.getTrackingNumber() != null && !req.getTrackingNumber().isBlank()) {
            order.setTrackingNumber(req.getTrackingNumber().trim());
        }
        if (req.getCarrier() != null && !req.getCarrier().isBlank()) {
            order.setCarrier(req.getCarrier().trim());
        }
        String note = (req.getNote() != null && !req.getNote().isBlank()) ? req.getNote() : "Status updated to " + status;
        OrderStatusHistory h = OrderStatusHistory.builder().order(order)
            .status(status).note(note).build();
        order.getStatusHistory().add(h);
        return toDto(orderRepository.save(order));
    }

    public PaginatedResponse<OrderDto> getAllOrders(int page, int pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC,"createdAt"));
        Page<Order> result = orderRepository.findAllByOrderByCreatedAtDesc(pageable);
        return PaginatedResponse.of(result.getContent().stream().map(this::toDto).toList(), result.getTotalElements(), page, pageSize);
    }

    private OrderDto toDto(Order o) {
        List<OrderItemDto> items = o.getItems() == null ? List.of() : o.getItems().stream().map(i ->
            OrderItemDto.builder().id(i.getId()).productId(i.getProductId())
                .productName(i.getProductName()).productImage(i.getProductImage())
                .sku(i.getSku()).productSku(i.getSku()).variantId(i.getVariantId())
                .quantity(i.getQuantity()).unitPrice(i.getUnitPrice()).totalPrice(i.getTotalPrice()).build()
        ).toList();
        List<StatusHistoryDto> history = o.getStatusHistory() == null ? List.of() : o.getStatusHistory().stream().map(h ->
            StatusHistoryDto.builder().status(h.getStatus()).timestamp(h.getCreatedAt()).note(h.getNote()).build()
        ).toList();
        String customerName = o.getUser() != null ? o.getUser().getFirstName() + " " + o.getUser().getLastName() : "Customer";
        String customerEmail = o.getUser() != null ? o.getUser().getEmail() : "";
        String customerPhone = o.getUser() != null && o.getUser().getPhone() != null ? o.getUser().getPhone() : "";

        return OrderDto.builder().id(o.getId()).orderNumber(o.getOrderNumber()).userId(o.getUser() != null ? o.getUser().getId() : null)
            .customerName(customerName).customerEmail(customerEmail).customerPhone(customerPhone)
            .status(o.getStatus()).paymentStatus(o.getPaymentStatus()).paymentMethod(o.getPaymentMethod())
            .items(items).shippingAddress(o.getShippingAddress())
            .subtotal(o.getSubtotal()).shippingCost(o.getShippingCost()).taxAmount(o.getTaxAmount())
            .discountAmount(o.getDiscountAmount()).totalAmount(o.getTotalAmount()).total(o.getTotalAmount())
            .couponCode(o.getCouponCode()).trackingNumber(o.getTrackingNumber()).carrier(o.getCarrier())
            .estimatedDelivery(o.getEstimatedDelivery()).deliveredAt(o.getDeliveredAt())
            .statusHistory(history).createdAt(o.getCreatedAt()).updatedAt(o.getUpdatedAt()).build();
    }
}
