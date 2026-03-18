package com.sagax.shop.model.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentDto {
    private Long orderId;
    private BigDecimal amount;
    private String cardNumber;
    private String cardHolderName;
}
