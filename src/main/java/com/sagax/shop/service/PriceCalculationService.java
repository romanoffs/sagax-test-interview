package com.sagax.shop.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

// CASE 32: Open-Closed Principle violation.
// Giant if/else chain for discount types. Adding a new discount type
// requires modifying this class instead of extending it.
// Should use Strategy pattern with polymorphic discount calculators.
@Service
public class PriceCalculationService {

    public BigDecimal calculateFinalPrice(BigDecimal originalPrice, String discountType, BigDecimal discountValue) {
        BigDecimal discount;

        if ("PERCENTAGE".equals(discountType)) {
            discount = originalPrice.multiply(discountValue)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        } else if ("FIXED".equals(discountType)) {
            discount = discountValue;
        } else if ("SEASONAL".equals(discountType)) {
            // Seasonal: 15% off
            discount = originalPrice.multiply(BigDecimal.valueOf(15))
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        } else if ("LOYALTY".equals(discountType)) {
            // Loyalty: 10% off
            discount = originalPrice.multiply(BigDecimal.valueOf(10))
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        } else if ("COUPON".equals(discountType)) {
            discount = discountValue != null ? discountValue : BigDecimal.ZERO;
        } else if ("EMPLOYEE".equals(discountType)) {
            // Employee discount: 25%
            discount = originalPrice.multiply(BigDecimal.valueOf(25))
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        } else if ("BULK".equals(discountType)) {
            // Bulk discount: 20%
            discount = originalPrice.multiply(BigDecimal.valueOf(20))
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        } else {
            discount = BigDecimal.ZERO;
        }

        BigDecimal finalPrice = originalPrice.subtract(discount);
        return finalPrice.compareTo(BigDecimal.ZERO) > 0 ? finalPrice : BigDecimal.ZERO;
    }
}
