package com.sagax.shop.service;

import com.sagax.shop.exception.PaymentException;
import com.sagax.shop.model.dto.PaymentDto;
import com.sagax.shop.model.entity.Order;
import com.sagax.shop.model.entity.Payment;
import com.sagax.shop.model.enums.PaymentStatus;
import com.sagax.shop.repository.OrderRepository;
import com.sagax.shop.repository.PaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
public class PaymentService {

    // CASE 15: Field injection instead of constructor injection.
    // Makes testing harder, hides dependencies, allows invalid object state.
    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    // CASE 12: Circular dependency — OrderService depends on PaymentService,
    // PaymentService depends on OrderService (via @Lazy workaround)
    @Autowired
    @org.springframework.context.annotation.Lazy
    private OrderService orderService;

    // CASE 8: @Transactional does NOT rollback on checked exceptions by default.
    // PaymentException extends Exception (checked), so even if it's thrown,
    // the transaction will COMMIT, leaving inconsistent data.
    // Fix: @Transactional(rollbackFor = PaymentException.class)
    @Transactional
    public Payment processPayment(PaymentDto paymentDto) throws PaymentException {
        log.info("Processing payment for order: {}", paymentDto.getOrderId());

        // CASE 35: Sensitive data in logs — leaking card number and PII
        log.info("Payment details — card: {}, amount: {}",
                paymentDto.getCardNumber(), paymentDto.getAmount());

        Order order = orderRepository.findById(paymentDto.getOrderId())
                .orElseThrow(() -> new PaymentException("Order not found: " + paymentDto.getOrderId()));

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(paymentDto.getAmount());
        payment.setTransactionId(UUID.randomUUID().toString());
        payment.setProcessedAt(LocalDateTime.now());

        // Simulate payment processing
        if (paymentDto.getCardNumber() == null || paymentDto.getCardNumber().length() < 13) {
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            throw new PaymentException("Invalid card number"); // checked exception — no rollback!
        }

        payment.setStatus(PaymentStatus.COMPLETED);
        paymentRepository.save(payment);

        // Update order status via OrderService
        orderService.markOrderPaid(order.getId());

        return payment;
    }

    public Payment getPaymentByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId)
                .orElse(null);
    }
}
