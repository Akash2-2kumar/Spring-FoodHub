package com.foodhub.foodhub.Repository;

import com.foodhub.foodhub.Entity.Food;
import com.foodhub.foodhub.Enum.FoodCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface FoodRepository extends JpaRepository<Food, Long> {

    List<Food> findByRestaurantId(Long restaurantId);
    @Query("""
        SELECT f FROM Food f
        WHERE (:name IS NULL OR LOWER(f.name) LIKE LOWER(CONCAT('%', :name, '%')))
        AND (:category IS NULL OR f.category = :category)
        AND (:maxPrice IS NULL OR f.price <= :maxPrice)
        """)
    List<Food> searchFoods(@Param("name") String name, @Param("category") FoodCategory category, @Param("maxPrice") BigDecimal maxPrice);
    @Query("SELECT f FROM Food f WHERE f.price < :price")
    List<Food> findFoodBelowPrice(BigDecimal price);

    @Query(value = "SELECT * FROM foods WHERE price < :price", nativeQuery = true)
    List<Food> findFoodBelowPriceNative(BigDecimal price);

    List<Food> findByNameContainingIgnoreCase(String name);

}