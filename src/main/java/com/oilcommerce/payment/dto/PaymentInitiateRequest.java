package com.oilcommerce.payment.dto;
import jakarta.validation.constraints.NotNull; import lombok.Data; import java.math.BigDecimal; import java.util.UUID;
@Data
public class PaymentInitiateRequest {
    @NotNull private UUID orderId;
    private BigDecimal amount;
}
