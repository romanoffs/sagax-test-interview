package com.sagax.shop.repository;

import com.sagax.shop.model.entity.Order;
import com.sagax.shop.model.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserId(Long userId);

    // CASE 24: N+1 in custom @Query — fetches orders but doesn't JOIN FETCH items or user.
    // When used in a report endpoint that accesses both items and user, triggers N+1.
    @Query("SELECT o FROM Order o WHERE o.status = :status")
    List<Order> findByStatus(OrderStatus status);

    @Query("SELECT o FROM Order o WHERE o.user.id = :userId AND o.status = :status")
    List<Order> findByUserIdAndStatus(Long userId, OrderStatus status);
}
