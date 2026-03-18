package com.sagax.shop.repository;

import com.sagax.shop.model.entity.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;

import java.util.List;

// CASE 11: @Component instead of @Repository.
// JPA-specific exceptions will NOT be translated to Spring's DataAccessException hierarchy.
// Should be @Repository for proper exception translation.
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
