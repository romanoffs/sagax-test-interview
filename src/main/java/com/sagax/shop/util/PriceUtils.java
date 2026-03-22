package com.sagax.shop.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PriceUtils {

    public static boolean arePricesEqual(BigDecimal price1, BigDecimal price2) {
        if (price1 == null || price2 == null) {
            return price1 == price2;
        }
        return price1.equals(price2);
    }

    public static BigDecimal applyDiscount(BigDecimal price, BigDecimal discountPercent) {
        BigDecimal discount = price.multiply(discountPercent).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        return price.subtract(discount);
    }

    public static BigDecimal calculateTotal(BigDecimal unitPrice, int quantity) {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
