package com.sagax.shop.util;

import com.sagax.shop.model.entity.Order;
import com.sagax.shop.model.entity.User;
import com.sagax.shop.model.enums.OrderStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderUtilsTest {

    static Stream<Arguments> hasStatusCases() {
        return Stream.of(
                Arguments.of(OrderStatus.PENDING, OrderStatus.PENDING, true),
                Arguments.of(OrderStatus.SHIPPED, OrderStatus.PENDING, false),
                Arguments.of(OrderStatus.CANCELLED, OrderStatus.CANCELLED, true),
                Arguments.of(OrderStatus.DELIVERED, OrderStatus.CANCELLED, false),
                Arguments.of(OrderStatus.CONFIRMED, OrderStatus.CONFIRMED, true)
        );
    }

    @ParameterizedTest
    @MethodSource("hasStatusCases")
    void hasStatus_predicateReturnsExpectedResult(OrderStatus orderStatus, OrderStatus checkedStatus, boolean expected) {
        Order order = orderWithStatus(orderStatus);
        assertEquals(expected, OrderUtils.hasStatus(checkedStatus).test(order));
    }


    static Stream<Arguments> isTotalAboveCases() {
        return Stream.of(
                Arguments.of("150.00", "100.00", true),  // strictly above
                Arguments.of("100.00", "100.00", false), // equal — exclusive boundary
                Arguments.of("50.00", "100.00", false), // below
                Arguments.of("0.01", "0.00", true),  // zero min → any positive passes
                Arguments.of("0.00", "0.00", false)  // zero equals zero
        );
    }

    @ParameterizedTest
    @MethodSource("isTotalAboveCases")
    void isTotalAbove_predicateReturnsExpectedResult(String amount, String min, boolean expected) {
        Order order = orderWithAmount(new BigDecimal(amount));
        assertEquals(expected, OrderUtils.isTotalAbove(new BigDecimal(min)).test(order));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "alice@example.com",
            "bob@example.com",
            "user+tag@domain.co.uk",
            "admin@shop.io",
            "test.user@sub.domain.org"
    })
    void toUserEmail_correctlyExtractsEmail(String email) {
        Order order = orderWithEmailAndTime(email, LocalDateTime.now());
        assertEquals(email, OrderUtils.toUserEmail().apply(order));
    }

    static Stream<Arguments> byCreatedAtDescCases() {
        LocalDateTime base = LocalDateTime.of(2024, 6, 1, 12, 0);
        return Stream.of(
                Arguments.of(base, base.minusDays(1), -1, "newer before older"),
                Arguments.of(base.minusDays(1), base, 1, "older after newer"),
                Arguments.of(base, base, 0, "equal timestamps"),
                Arguments.of(base.plusHours(3), base.minusHours(3), -1, "same day different hours"),
                Arguments.of(base.minusYears(1), base, 1, "year difference")
        );
    }

    @ParameterizedTest(name = "{3}")
    @MethodSource("byCreatedAtDescCases")
    void byCreatedAtDesc_comparatorReturnsExpectedSign(LocalDateTime t1, LocalDateTime t2,
                                                       int expectedSign, String description) {
        Order o1 = orderWithEmailAndTime("a@a.com", t1);
        Order o2 = orderWithEmailAndTime("b@b.com", t2);
        int actual = OrderUtils.byCreatedAtDesc().compare(o1, o2);
        assertEquals(Integer.signum(expectedSign), Integer.signum(actual));
    }

    static Stream<OrderStatus> singleStatusGroupCases() {
        return Stream.of(OrderStatus.PENDING, OrderStatus.SHIPPED, OrderStatus.CANCELLED,
                OrderStatus.CONFIRMED, OrderStatus.DELIVERED);
    }

    @ParameterizedTest
    @MethodSource("singleStatusGroupCases")
    void groupOrdersByStatus_allSameStatusProducesSingleEntryMap(OrderStatus status) {
        List<Order> orders = List.of(orderWithStatus(status), orderWithStatus(status));
        Map<OrderStatus, List<Order>> result = OrderUtils.groupOrdersByStatus().apply(orders);
        assertEquals(1, result.size());
        assertEquals(2, result.get(status).size());
    }

    @Test
    void groupOrdersByStatus_mixedStatusesAreGroupedCorrectly() {
        List<Order> orders = List.of(
                orderWithStatus(OrderStatus.PENDING),
                orderWithStatus(OrderStatus.CONFIRMED),
                orderWithStatus(OrderStatus.PENDING)
        );
        Map<OrderStatus, List<Order>> result = OrderUtils.groupOrdersByStatus().apply(orders);
        assertEquals(2, result.get(OrderStatus.PENDING).size());
        assertEquals(1, result.get(OrderStatus.CONFIRMED).size());
    }

    @Test
    void groupOrdersByStatus_eachGroupContainsOnlyOrdersWithThatStatus() {
        List<Order> orders = List.of(
                orderWithStatus(OrderStatus.CANCELLED),
                orderWithStatus(OrderStatus.PENDING),
                orderWithStatus(OrderStatus.CANCELLED)
        );
        Map<OrderStatus, List<Order>> result = OrderUtils.groupOrdersByStatus().apply(orders);
        result.forEach((status, group) ->
                group.forEach(o -> assertEquals(status, o.getStatus()))
        );
    }

    @Test
    void findMostExpensiveOrder_emptyListReturnsEmptyOptional() {
        assertTrue(OrderUtils.findMostExpensiveOrder().apply(List.of()).isEmpty());
    }

    static Stream<Arguments> findMostExpensiveCases() {
        return Stream.of(
                Arguments.of(List.of("99.99"), "99.99", "single element"),
                Arguments.of(List.of("10.00", "500.00", "200.00"), "500.00", "max in middle"),
                Arguments.of(List.of("100.00", "100.00", "100.00"), "100.00", "all equal"),
                Arguments.of(List.of("50.00", "999.00"), "999.00", "max is last"),
                Arguments.of(List.of("1.00", "2.00", "3.00"), "3.00", "ascending order")
        );
    }

    @ParameterizedTest(name = "{2}")
    @MethodSource("findMostExpensiveCases")
    void findMostExpensiveOrder_returnsOrderWithHighestAmount(List<String> amounts, String expectedMax,
                                                              String description) {
        List<Order> orders = amounts.stream()
                .map(a -> orderWithAmount(new BigDecimal(a)))
                .toList();
        Optional<Order> result = OrderUtils.findMostExpensiveOrder().apply(orders);
        assertTrue(result.isPresent());
        assertEquals(new BigDecimal(expectedMax), result.get().getTotalAmount());
    }
    
    private Order orderWithStatus(OrderStatus status) {
        Order order = new Order();
        order.setStatus(status);
        order.setTotalAmount(BigDecimal.TEN);
        order.setCreatedAt(LocalDateTime.now());
        return order;
    }

    private Order orderWithAmount(BigDecimal amount) {
        Order order = new Order();
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(amount);
        order.setCreatedAt(LocalDateTime.now());
        return order;
    }

    private Order orderWithEmailAndTime(String email, LocalDateTime createdAt) {
        User user = new User();
        user.setEmail(email);
        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(BigDecimal.TEN);
        order.setCreatedAt(createdAt);
        return order;
    }
}
