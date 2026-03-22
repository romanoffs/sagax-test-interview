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

    public List<String> generateSalesReport() {
        List<String> reportLines = new ArrayList<>();

        long orderCount = orderRepository.findAll().stream()
                .filter(order -> order.getTotalAmount().compareTo(BigDecimal.valueOf(100)) > 0)
                .peek(order -> {
                    reportLines.add("Order #" + order.getId() + ": $" + order.getTotalAmount());
                })
                .count();

        log.info("Generated report for {} orders", orderCount);
        return reportLines;
    }

    public Map<String, BigDecimal> calculateCategoryTotals() {
        Map<String, BigDecimal> categoryTotals = new HashMap<>();

        orderRepository.findAll().parallelStream()
                .flatMap(order -> order.getItems().stream())
                .forEach(item -> {
                    String categoryName = item.getProduct().getCategory() != null
                            ? item.getProduct().getCategory().getName()
                            : "Uncategorized";
                    BigDecimal currentTotal = categoryTotals.getOrDefault(categoryName, BigDecimal.ZERO);
                    categoryTotals.put(categoryName, currentTotal.add(
                            item.getPriceAtPurchase().multiply(BigDecimal.valueOf(item.getQuantity()))));
                });

        return categoryTotals;
    }
}
