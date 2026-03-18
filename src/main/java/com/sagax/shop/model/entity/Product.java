package com.sagax.shop.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String sku;

    private BigDecimal price;

    private Integer stockQuantity;

    private String description;

    // CASE 19 (related): EAGER on ManyToOne is the JPA default, but worth discussing
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    @JsonIgnoreProperties("products")
    private Category category;

    @Version
    private Long version;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // CASE 1: Broken equals/hashCode contract.
    // equals() uses id + name + price, but hashCode() uses only id.
    // For new (unsaved) entities, id is null — all go to the same bucket in HashMap.
    // Two Products with same id but different name/price: equal hashCode, but equals returns false — OK.
    // Two Products with different id but same name+price: different hashCode, equals returns false — OK.
    // BUT: two NEW (unsaved) products with same name+price — hashCode is same (null),
    // equals is true — they are "equal" which may not be intended.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id) &&
                Objects.equals(name, product.name) &&
                Objects.equals(price, product.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
