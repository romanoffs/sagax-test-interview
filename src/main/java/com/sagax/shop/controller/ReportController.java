package com.sagax.shop.controller;

import com.sagax.shop.service.OrderService;
import com.sagax.shop.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final OrderService orderService;

    @GetMapping("/sales")
    public ResponseEntity<List<String>> getSalesReport() {
        return ResponseEntity.ok(reportService.generateSalesReport());
    }

    @GetMapping("/category-totals")
    public ResponseEntity<Map<String, BigDecimal>> getCategoryTotals() {
        return ResponseEntity.ok(reportService.calculateCategoryTotals());
    }

    @GetMapping("/revenue")
    public ResponseEntity<BigDecimal> getTotalRevenue() {
        return ResponseEntity.ok(orderService.calculateTotalRevenue());
    }
}
