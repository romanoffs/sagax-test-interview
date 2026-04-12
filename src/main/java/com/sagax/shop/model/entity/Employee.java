package com.sagax.shop.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Employee {
    @Id
    private Long id;

    private String name;

    private String position;
}
