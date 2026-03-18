package com.sagax.shop.controller;

import com.sagax.shop.model.dto.CreateOrderRequest;
import com.sagax.shop.model.dto.OrderDto;
import com.sagax.shop.model.entity.Order;
import com.sagax.shop.model.entity.OrderItem;
import com.sagax.shop.model.enums.OrderStatus;
import com.sagax.shop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // CASE 17: N+1 problem — fetches all orders, then for EACH order
    // iterates order.getItems() triggering a separate SELECT per order.
    // No JOIN FETCH or @EntityGraph is used.
    @GetMapping
    public ResponseEntity<List<OrderDto>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        List<OrderDto> dtos = new ArrayList<>();

        for (Order order : orders) {
            OrderDto dto = new OrderDto();
            dto.setId(order.getId());
            dto.setUserId(order.getUser().getId()); // additional SELECT for user
            dto.setStatus(order.getStatus().name());
            dto.setTotalAmount(order.getTotalAmount());
            dto.setCreatedAt(order.getCreatedAt());

            // N+1: each order.getItems() triggers a separate query
            List<OrderDto.OrderItemDto> itemDtos = new ArrayList<>();
            for (OrderItem item : order.getItems()) {
                OrderDto.OrderItemDto itemDto = new OrderDto.OrderItemDto();
                itemDto.setProductId(item.getProduct().getId());
                itemDto.setProductName(item.getProduct().getName()); // yet another SELECT
                itemDto.setQuantity(item.getQuantity());
                itemDto.setPrice(item.getPriceAtPurchase());
                itemDtos.add(itemDto);
            }
            dto.setItems(itemDtos);
            dtos.add(dto);
        }

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        // CASE 28: OrderNotFoundException NOT handled in GlobalExceptionHandler — returns 500
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody CreateOrderRequest request) {
        return ResponseEntity.ok(orderService.createOrder(request));
    }

    // CASE 26: Non-idempotent PUT.
    // PUT should be idempotent, but updateOrderStatus() sends a notification email
    // on EVERY call. Calling PUT twice sends two emails.
    // Also: using PUT for partial update (only status), where PATCH is more appropriate.
    @PutMapping("/{id}/status")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable Long id,
                                                   @RequestParam OrderStatus status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}
