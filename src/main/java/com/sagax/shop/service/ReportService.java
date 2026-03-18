package com.sagax.shop.service;

import com.sagax.shop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {

    private final OrderRepository orderRepository;

    // CASE 3: Stream API — side-effect in peek() treated as operation.
    // peek() is used to accumulate into a list, but the terminal operation is count().
    // In Java 9+, count() may short-circuit and skip peek() entirely for some sources.
    // Also: modifying an external list inside peek() is a side-effect anti-pattern.
    public List<String> generateSalesReport() {
        List<String> reportLines = new ArrayList<>();

        long orderCount = orderRepository.findAll().stream()
                .filter(order -> order.getTotalAmount().compareTo(BigDecimal.valueOf(100)) > 0)
                .peek(order -> {
                    // Side effect in peek — anti-pattern!
                    // Also, count() may skip peek() in some cases (Java 9+ optimization)
                    reportLines.add("Order #" + order.getId() + ": $" + order.getTotalAmount());
                })
                .count();

        log.info("Generated report for {} orders", orderCount);
        return reportLines; // May be empty if peek() was skipped!
    }

    // CASE 4: parallel() stream on non-thread-safe collection.
    // Using parallelStream() to accumulate results into a plain HashMap — race condition.
    // Multiple threads writing to HashMap concurrently can cause data corruption.
    public Map<String, BigDecimal> calculateCategoryTotals() {
        Map<String, BigDecimal> categoryTotals = new HashMap<>(); // NOT thread-safe!

        orderRepository.findAll().parallelStream() // parallel on non-thread-safe collection!
                .flatMap(order -> order.getItems().stream())
                .forEach(item -> {
                    String categoryName = item.getProduct().getCategory() != null
                            ? item.getProduct().getCategory().getName()
                            : "Uncategorized";
                    // Race condition: multiple threads reading/writing HashMap concurrently
                    BigDecimal currentTotal = categoryTotals.getOrDefault(categoryName, BigDecimal.ZERO);
                    categoryTotals.put(categoryName, currentTotal.add(
                            item.getPriceAtPurchase().multiply(BigDecimal.valueOf(item.getQuantity()))));
                });

        return categoryTotals;
    }
}
