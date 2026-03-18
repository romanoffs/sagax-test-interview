package com.sagax.shop.service;

import com.sagax.shop.exception.OrderNotFoundException;
import com.sagax.shop.model.dto.CreateOrderRequest;
import com.sagax.shop.model.entity.Order;
import com.sagax.shop.model.entity.OrderItem;
import com.sagax.shop.model.entity.Product;
import com.sagax.shop.model.entity.User;
import com.sagax.shop.model.enums.OrderStatus;
import com.sagax.shop.repository.OrderRepository;
import com.sagax.shop.repository.UserRepository;
import com.sagax.shop.util.PriceUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// CASE 16: God class / SRP violation.
// This service handles: order creation, payment processing, inventory updates,
// email notifications, PDF receipt generation, and report queries — all in one class.
@Service
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductService productService;
    private final PaymentService paymentService;
    private final NotificationService notificationService;

    // CASE 12: Circular dependency — OrderService → PaymentService → OrderService
    // PaymentService uses @Lazy to break the cycle, which is itself a code smell.
    public OrderService(OrderRepository orderRepository,
                        UserRepository userRepository,
                        ProductService productService,
                        PaymentService paymentService,
                        NotificationService notificationService) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.productService = productService;
        this.paymentService = paymentService;
        this.notificationService = notificationService;
    }

    @Transactional
    public Order createOrder(CreateOrderRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());

        List<OrderItem> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (CreateOrderRequest.OrderItemRequest itemReq : request.getItems()) {
            Product product = productService.getProductById(itemReq.getProductId());

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setQuantity(itemReq.getQuantity());
            item.setPriceAtPurchase(product.getPrice());
            items.add(item);

            total = total.add(PriceUtils.calculateTotal(product.getPrice(), itemReq.getQuantity()));
        }

        order.setItems(items);
        order.setTotalAmount(total);

        Order savedOrder = orderRepository.save(order);

        // CASE 7: Self-invocation — calling internal method annotated with @Transactional(REQUIRES_NEW).
        // Because this is a direct method call (this.processPaymentInternal()),
        // the proxy is bypassed and REQUIRES_NEW has no effect.
        processPaymentInternal(savedOrder);

        // CASE 34: DIP violation — creating dependency directly instead of injection
        sendReceiptEmail(savedOrder, user);

        return savedOrder;
    }

    // CASE 7: This annotation has NO EFFECT because it's called internally (self-invocation).
    // The Spring proxy is bypassed, so REQUIRES_NEW never creates a new transaction.
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processPaymentInternal(Order order) {
        log.info("Processing payment for order: {}", order.getId());
        // Payment processing logic that should run in its own transaction
        // but actually runs in the caller's transaction due to self-invocation
    }

    // CASE 9: @Transactional on a private method — Spring AOP proxy cannot intercept private methods.
    // This annotation is completely ignored.
    @Transactional
    private void updateInventory(Order order) {
        for (OrderItem item : order.getItems()) {
            productService.purchaseProduct(item.getProduct().getId(), item.getQuantity());
        }
    }

    // CASE 34: Dependency Inversion Principle violation — creating EmailSender directly
    // instead of depending on an injected abstraction. Untestable, tightly coupled.
    private void sendReceiptEmail(Order order, User user) {
        // Direct instantiation instead of DI
        // In a real app, this would be: new EmailSender() or similar
        try {
            // Simulating direct file-based "email" sending — tightly coupled to file system
            String receipt = generateReceipt(order);
            log.info("Sending receipt email to: {}", user.getEmail());
            // Simulate writing receipt to file (tight coupling)
            try (FileWriter writer = new FileWriter("/tmp/receipt_" + order.getId() + ".txt")) {
                writer.write(receipt);
            }
        } catch (IOException e) {
            log.error("Failed to send receipt", e);
        }
    }

    // Part of CASE 16: PDF receipt generation shouldn't be in OrderService
    private String generateReceipt(Order order) {
        StringBuilder sb = new StringBuilder();
        sb.append("Order Receipt #").append(order.getId()).append("\n");
        sb.append("Date: ").append(order.getCreatedAt()).append("\n");
        sb.append("Total: $").append(order.getTotalAmount()).append("\n");
        sb.append("---\n");
        for (OrderItem item : order.getItems()) {
            sb.append(item.getProduct().getName())
                    .append(" x").append(item.getQuantity())
                    .append(" = $").append(item.getPriceAtPurchase())
                    .append("\n");
        }
        return sb.toString();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    // CASE 26 (related): called from PUT endpoint that has side effects
    @Transactional
    public Order updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = getOrderById(orderId);
        order.setStatus(status);

        // Side effect: logging to external system on every call makes PUT non-idempotent
        log.info("Order {} status changed to {} — sending notification", orderId, status);
        notificationService.sendEmail(
                order.getUser().getEmail(),
                "Order Status Update",
                "Your order #" + orderId + " is now " + status
        );

        return orderRepository.save(order);
    }

    @Transactional
    public void markOrderPaid(Long orderId) {
        Order order = getOrderById(orderId);
        order.setStatus(OrderStatus.CONFIRMED);
        orderRepository.save(order);
    }

    @Transactional
    public void deleteOrder(Long orderId) {
        Order order = getOrderById(orderId);
        orderRepository.delete(order);
    }

    // Part of CASE 16: Report query logic shouldn't be in OrderService
    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    // Part of CASE 16: Yet another responsibility in the God class
    public BigDecimal calculateTotalRevenue() {
        return orderRepository.findAll().stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
