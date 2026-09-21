package com.oilcommerce.payment.service;

import com.oilcommerce.exception.ResourceNotFoundException;
import com.oilcommerce.order.entity.Order;
import com.oilcommerce.order.entity.PaymentStatus;
import com.oilcommerce.order.repository.OrderRepository;
import com.oilcommerce.payment.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j @Service @RequiredArgsConstructor
public class PaymentService {

    private final OrderRepository orderRepository;

    @Value("${razorpay.key-id:rzp_test_placeholder}")
    private String keyId;

    @Value("${razorpay.key-secret:rzp_secret_placeholder}")
    private String keySecret;

    public PaymentInitiateResponse initiatePayment(UUID userId, PaymentInitiateRequest req) {
        Order order = orderRepository.findById(req.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order","id",req.getOrderId()));

        // In production: create Razorpay order via API
        // For now, return a mock response structure that Razorpay SDK expects
        String razorpayOrderId = "order_" + UUID.randomUUID().toString().replace("-","").substring(0,16);

        return PaymentInitiateResponse.builder()
                .razorpayOrderId(razorpayOrderId)
                .keyId(keyId)
                .amount(order.getTotalAmount().multiply(java.math.BigDecimal.valueOf(100)).longValue())
                .currency("INR")
                .orderId(order.getId().toString())
                .build();
    }

    @Transactional
    public boolean verifyPayment(PaymentVerifyRequest req) {
        try {
            String payload = req.getRazorpayOrderId() + "|" + req.getRazorpayPaymentId();
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(keySecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) hex.append(String.format("%02x", b));
            boolean valid = hex.toString().equals(req.getRazorpaySignature());

            if (valid) {
                orderRepository.findByRazorpayOrderId(req.getRazorpayOrderId()).ifPresent(order -> {
                    order.setPaymentStatus(PaymentStatus.SUCCESS);
                    order.setRazorpayPaymentId(req.getRazorpayPaymentId());
                    orderRepository.save(order);
                });
            }
            return valid;
        } catch (java.security.NoSuchAlgorithmException | java.security.InvalidKeyException e) {
            log.error("Payment verification failed: {}", e.getMessage());
            return false;
        }
    }

    public List<Map<String,String>> getPaymentMethods() {
        return List.of(
            Map.of("id","razorpay","name","Razorpay (UPI / Cards / NetBanking)","icon","💳"),
            Map.of("id","cod","name","Cash on Delivery","icon","💵"),
            Map.of("id","bank_transfer","name","Bank Transfer","icon","🏦")
        );
    }
}
