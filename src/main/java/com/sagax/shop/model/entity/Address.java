package com.sagax.shop.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({"addresses", "orders"})
    private User user;

    private String city;

    private String street;

    private String zipCode;

    // CASE 21: This field exists in the entity but NOT in the Flyway V1 migration.
    // Only created because of ddl-auto: update — fragile, won't work if ddl-auto is 'validate'.
    private String country;
}
