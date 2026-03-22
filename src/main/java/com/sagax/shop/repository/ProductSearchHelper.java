package com.sagax.shop.repository;

import com.sagax.shop.model.entity.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductSearchHelper {

    @PersistenceContext
    private EntityManager entityManager;

    @SuppressWarnings("unchecked")
    public List<Product> searchByName(String keyword) {
        return entityManager.createQuery(
                        "SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(:keyword)")
                .setParameter("keyword", "%" + keyword + "%")
                .getResultList();
    }
}
