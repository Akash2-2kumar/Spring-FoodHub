package com.foodhub.foodhub.Mapper;

import com.foodhub.foodhub.Dto.Request.RestaurantRequest;
import com.foodhub.foodhub.Dto.Response.RestaurantResponse;
import com.foodhub.foodhub.Entity.Restaurant;
import com.foodhub.foodhub.Entity.User;
import org.springframework.stereotype.Component;

@Component
public class RestaurantMapper {

    public Restaurant toEntity(
            RestaurantRequest request,
            User owner) {

        return Restaurant.builder()
                .name(request.getName())
                .address(request.getAddress())
                .phone(request.getPhone())
                .owner(owner)
                .build();
    }

    public RestaurantResponse toResponse(
            Restaurant restaurant) {

        return RestaurantResponse.builder()
                .id(restaurant.getId())
                .name(restaurant.getName())
                .address(restaurant.getAddress())
                .phone(restaurant.getPhone())
                .active(restaurant.getActive())
                .ownerId(restaurant.getOwner().getId())
                .build();
    }
}