package com.oilcommerce.order.entity;

import com.oilcommerce.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "order_status_history")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OrderStatusHistory extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "order_id", nullable = false) private Order order;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private OrderStatus status;
    @Column private String note;

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public static OrderStatusHistoryBuilder builder() {
        return new OrderStatusHistoryBuilder();
    }

    public static class OrderStatusHistoryBuilder {
        private Order order;
        private OrderStatus status;
        private String note;
        private String updatedBy;

        public OrderStatusHistoryBuilder order(Order order) { this.order = order; return this; }
        public OrderStatusHistoryBuilder status(OrderStatus status) { this.status = status; return this; }
        public OrderStatusHistoryBuilder note(String note) { this.note = note; return this; }
        public OrderStatusHistoryBuilder updatedBy(String updatedBy) { this.updatedBy = updatedBy; return this; }

        public OrderStatusHistory build() {
            OrderStatusHistory history = new OrderStatusHistory();
            history.setOrder(this.order);
            history.setStatus(this.status);
            history.setNote(this.note);
            history.setUpdatedBy(this.updatedBy);
            return history;
        }
    }
}
