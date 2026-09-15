package com.oilcommerce.payment.dto;
import lombok.Builder; import lombok.Data;
@Data @Builder
public class PaymentInitiateResponse {
    private String razorpayOrderId; private String keyId;
    private Long amount; private String currency; private String orderId;
}
