package com.sagax.shop.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.List;

@Entity
public record Department(@Id Long id,
                         String name,
                         @OneToMany List<Employee> employees) {}
