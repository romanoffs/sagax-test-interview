package com.sagax.shop.service;

import com.sagax.shop.model.entity.Order;
import com.sagax.shop.model.enums.OrderStatus;
import com.sagax.shop.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @MockitoBean
    private OrderRepository orderRepository;

    @Test
    void testGetOrdersByStatus() {
        Order order1 = new Order();
        order1.setId(1L);
        order1.setStatus(OrderStatus.PENDING);
        order1.setTotalAmount(new BigDecimal("100.00"));

        Order order2 = new Order();
        order2.setId(2L);
        order2.setStatus(OrderStatus.PENDING);
        order2.setTotalAmount(new BigDecimal("200.00"));

        when(orderRepository.findByStatus(OrderStatus.PENDING))
                .thenReturn(Arrays.asList(order1, order2));

        List<Order> result = orderService.getOrdersByStatus(OrderStatus.PENDING);

        assertEquals(2, result.size());
    }
}
