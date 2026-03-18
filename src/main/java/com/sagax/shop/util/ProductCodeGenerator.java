package com.sagax.shop.util;

import java.util.concurrent.atomic.AtomicLong;

public class ProductCodeGenerator {

    private static final AtomicLong counter = new AtomicLong(0);

    public static String generateSku(String categoryPrefix) {
        return categoryPrefix + "-" + String.format("%04d", counter.incrementAndGet());
    }
}
