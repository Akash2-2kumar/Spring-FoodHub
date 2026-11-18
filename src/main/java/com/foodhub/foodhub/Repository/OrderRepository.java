package com.foodhub.foodhub.Repository;

import com.foodhub.foodhub.Entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserId(Long userId);
    List<Order> findByDeliveryBoyId(Long deliveryBoyId);
    @Query("""
    SELECT DISTINCT o
    FROM Order o
    JOIN OrderItem oi ON oi.order = o
    JOIN Food f ON oi.food = f
    WHERE f.restaurant.owner.id = :ownerId""")
    List<Order> findOrdersByRestaurantOwner(@Param("ownerId") Long ownerId);
}