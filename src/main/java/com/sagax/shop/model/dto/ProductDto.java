package com.sagax.shop.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductDto {

    private Long id;

    @NotBlank(message = "Product name is required")
    private String name;

    private String sku;

    @NotNull(message = "Price is required")
    @Min(value = 0, message = "Price must be non-negative")
    private BigDecimal price;

    @Min(value = 0, message = "Stock quantity must be non-negative")
    private Integer stockQuantity;

    private String description;
    private Long categoryId;
}
