package com.sagax.shop.controller;

import com.sagax.shop.exception.PaymentException;
import com.sagax.shop.model.dto.PaymentDto;
import com.sagax.shop.model.entity.Payment;
import com.sagax.shop.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<Payment> processPayment(@RequestBody PaymentDto paymentDto) {
        try {
            Payment payment = paymentService.processPayment(paymentDto);
            return ResponseEntity.ok(payment);
        } catch (PaymentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<Payment> getPaymentByOrder(@PathVariable Long orderId) {
        Payment payment = paymentService.getPaymentByOrderId(orderId);
        if (payment == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(payment);
    }
}
