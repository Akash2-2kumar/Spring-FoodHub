package com.foodhub.foodhub.Mapper;

import com.foodhub.foodhub.Dto.Request.FoodRequest;
import com.foodhub.foodhub.Dto.Response.FoodResponse;
import com.foodhub.foodhub.Entity.Food;
import com.foodhub.foodhub.Entity.Restaurant;
import org.springframework.stereotype.Component;

@Component
public class FoodMapper {

    public Food toEntity(FoodRequest request, Restaurant restaurant) {
        return Food.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .category(request.getCategory())
                .restaurant(restaurant)
                .build();
    }

    public FoodResponse toResponse(Food food) {
        return FoodResponse.builder()
                .id(food.getId())
                .name(food.getName())
                .description(food.getDescription())
                .price(food.getPrice())
                .stock(food.getStock())
                .category(food.getCategory())
                .restaurantId(food.getRestaurant().getId())
                .build();
    }
}