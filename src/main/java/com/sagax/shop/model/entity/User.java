package com.sagax.shop.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String firstName;

    private String lastName;

    // CASE 27 (related): Plain text password stored — should be hashed
    private String password;

    private String role;

    // CASE 18: Lazy-loaded collection — will throw LazyInitializationException
    // if accessed outside transactional context
    @OneToMany(mappedBy = "user")
    @JsonIgnoreProperties("user")
    private List<Order> orders = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("user")
    private List<Address> addresses = new ArrayList<>();
}
