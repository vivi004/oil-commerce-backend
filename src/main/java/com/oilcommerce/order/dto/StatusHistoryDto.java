package com.oilcommerce.order.dto;

import com.oilcommerce.order.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatusHistoryDto {
    private OrderStatus status;
    private Instant timestamp;
    private String note;

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public static StatusHistoryDtoBuilder builder() {
        return new StatusHistoryDtoBuilder();
    }

    public static class StatusHistoryDtoBuilder {
        private OrderStatus status;
        private Instant timestamp;
        private String note;

        public StatusHistoryDtoBuilder status(OrderStatus status) { this.status = status; return this; }
        public StatusHistoryDtoBuilder timestamp(Instant timestamp) { this.timestamp = timestamp; return this; }
        public StatusHistoryDtoBuilder note(String note) { this.note = note; return this; }

        public StatusHistoryDto build() {
            StatusHistoryDto dto = new StatusHistoryDto();
            dto.setStatus(this.status);
            dto.setTimestamp(this.timestamp);
            dto.setNote(this.note);
            return dto;
        }
    }
}
