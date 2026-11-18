package com.foodhub.foodhub.Repository;

import com.foodhub.foodhub.Entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    List<Restaurant> findByName(String name);

    List<Restaurant> findByOwnerId(Long ownerId);
}