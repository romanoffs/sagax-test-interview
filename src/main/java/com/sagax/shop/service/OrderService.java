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

@Service
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductService productService;
    private final PaymentService paymentService;
    private final NotificationService notificationService;

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

        processPaymentInternal(savedOrder);

        sendReceiptEmail(savedOrder, user);

        return savedOrder;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processPaymentInternal(Order order) {
        log.info("Processing payment for order: {}", order.getId());
    }

    @Transactional
    private void updateInventory(Order order) {
        for (OrderItem item : order.getItems()) {
            productService.purchaseProduct(item.getProduct().getId(), item.getQuantity());
        }
    }

    private void sendReceiptEmail(Order order, User user) {
        try {
            String receipt = generateReceipt(order);
            log.info("Sending receipt email to: {}", user.getEmail());
            try (FileWriter writer = new FileWriter("/tmp/receipt_" + order.getId() + ".txt")) {
                writer.write(receipt);
            }
        } catch (IOException e) {
            log.error("Failed to send receipt", e);
        }
    }

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

    @Transactional
    public Order updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = getOrderById(orderId);
        order.setStatus(status);

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

    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    public BigDecimal calculateTotalRevenue() {
        return orderRepository.findAll().stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
