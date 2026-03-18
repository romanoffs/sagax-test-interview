package com.sagax.shop.model.enums;

// CASE 22: Used with @Enumerated(EnumType.ORDINAL) in Order entity.
// If someone inserts a new status in the middle (e.g., PAYMENT_PENDING between PENDING and CONFIRMED),
// all existing DB data will be silently corrupted.
public enum OrderStatus {
    PENDING,
    CONFIRMED,
    SHIPPED,
    DELIVERED,
    CANCELLED
}
