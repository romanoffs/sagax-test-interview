package com.sagax.shop.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sagax.shop.model.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({"orders", "addresses", "password"})
    private User user;

    // CASE 23: Missing orphanRemoval = true.
    // Removing an item from this list (order.getItems().remove(item))
    // will NOT delete the orphan OrderItem from the database.
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("order")
    private List<OrderItem> items = new ArrayList<>();

    // CASE 22: EnumType.ORDINAL — if enum constants are reordered or a new one
    // is inserted in the middle, all existing DB data is silently corrupted.
    @Enumerated(EnumType.ORDINAL)
    private OrderStatus status;

    private BigDecimal totalAmount;

    private LocalDateTime createdAt;

    @Version
    private Long version;
}
