package com.oilcommerce.order.repository;
import com.oilcommerce.order.entity.Order;
import com.oilcommerce.order.entity.OrderStatus;
import org.springframework.data.domain.*; import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.Optional; import java.util.UUID;
@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    @EntityGraph(attributePaths = {"user", "items"})
    Page<Order> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);
    Optional<Order> findByOrderNumberAndUserId(String orderNumber, UUID userId);
    Optional<Order> findByRazorpayOrderId(String razorpayOrderId);

    @EntityGraph(attributePaths = {"user", "items"})
    Page<Order> findAllByOrderByCreatedAtDesc(Pageable pageable);
    long countByDeletedFalse();
    long countByStatusAndDeletedFalse(OrderStatus status);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.deleted = false AND o.status != com.oilcommerce.order.entity.OrderStatus.CANCELLED")
    BigDecimal calculateTotalRevenue();
}
