package com.sagax.shop.util;

import com.sagax.shop.exception.ExerciseNotCompletedException;
import com.sagax.shop.model.entity.Order;
import com.sagax.shop.model.enums.OrderStatus;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.*;

/**
 * {@link OrderUtils} is an exercise class focused on {@link Order} domain operations.
 * Each method must be implemented using a lambda expression or a method reference.
 * Every unimplemented method throws {@link ExerciseNotCompletedException}.
 * <p>
 * TODO: remove the exception and implement each method using lambda or method reference
 */
public class OrderUtils {

    /**
     * Returns a {@link Predicate} that checks whether an order has the given {@link OrderStatus}.
     *
     * @param status the expected status
     * @return predicate that matches orders with the given status
     */
    // CASE 38: Functional Interface: Predicate implementation
    public static Predicate<Order> hasStatus(OrderStatus status) {
        throw new ExerciseNotCompletedException();
    }

    /**
     * Returns a {@link Predicate} that checks whether an order's total amount is greater than
     * the provided minimum value.
     *
     * @param min the minimum amount (exclusive)
     * @return predicate for orders above a minimum total
     */
    // CASE 39: Functional Interface: Predicate with BigDecimal implementation
    public static Predicate<Order> isTotalAbove(BigDecimal min) {
        throw new ExerciseNotCompletedException();
    }

    /**
     * Returns a {@link Function} that maps an {@link Order} to the email address of its associated user.
     *
     * @return function extracting the user email from an order
     */
    // CASE 40: Functional Interface: Function implementation
    public static Function<Order, String> toUserEmail() {
        throw new ExerciseNotCompletedException();
    }

    /**
     * Returns a {@link Comparator} that compares {@link Order} objects by their {@code createdAt} timestamp
     * in descending order (newest first).
     * <p>
     * PLEASE NOTE: {@link Comparator} is a functional interface. Write a lambda manually — do not use
     * {@code Comparator.comparing()}.
     *
     * @return comparator by creation time descending
     */
    // CASE 41: Comparator Implementation without comparing()
    public static Comparator<Order> byCreatedAtDesc() {
        throw new ExerciseNotCompletedException();
    }

    /**
     * Returns a {@link Function} that accepts a list of orders and groups them into a {@link Map}
     * where the key is the {@link OrderStatus} and the value is the list of matching orders.
     *
     * @return function that groups orders by status
     */
    // CASE 42: Stream API: groupingBy Collector
    public static Function<List<Order>, Map<OrderStatus, List<Order>>> groupOrdersByStatus() {
        throw new ExerciseNotCompletedException();
    }

    /**
     * Returns a {@link Function} that accepts a list of orders and returns an {@link Optional}
     * containing the order with the highest {@code totalAmount}, or {@link Optional#empty()} if
     * the list is empty.
     *
     * @return function that finds the most expensive order
     */
    // CASE 43: Stream API: max reduction
    public static Function<List<Order>, Optional<Order>> findMostExpensiveOrder() {
        throw new ExerciseNotCompletedException();
    }
}
