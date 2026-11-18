package com.foodhub.foodhub.Controller;

import com.foodhub.foodhub.Dto.Request.FoodRequest;
import com.foodhub.foodhub.Dto.Response.FoodResponse;
import com.foodhub.foodhub.Enum.FoodCategory;
import com.foodhub.foodhub.Service.FoodService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/foods")
@RequiredArgsConstructor
public class FoodController {

    private final FoodService foodService;

    @PostMapping
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<FoodResponse> createFood(Authentication authentication, @Valid @RequestBody FoodRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(foodService.createFood(request,authentication));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'RESTAURANT_OWNER', 'ADMIN')")
    public ResponseEntity<FoodResponse> getFoodById(@PathVariable Long id) {

        return ResponseEntity.ok(foodService.getFoodById(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('CUSTOMER', 'RESTAURANT_OWNER', 'ADMIN')")
    public ResponseEntity<List<FoodResponse>> getAllFoods() {
        return ResponseEntity.ok(foodService.getAllFoods()
        );
    }

    @GetMapping("/restaurant/{restaurantId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'RESTAURANT_OWNER', 'ADMIN')")
    public ResponseEntity<List<FoodResponse>> getFoodsByRestaurant(@PathVariable Long restaurantId) {

        return ResponseEntity.ok(foodService.getFoodsByRestaurant(restaurantId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('RESTAURANT_OWNER', 'ADMIN')")
    public ResponseEntity<Void> deleteFood(@PathVariable Long id,Authentication authentication) {
        foodService.deleteFood(id,authentication);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'RESTAURANT_OWNER', 'ADMIN')")
    public ResponseEntity<List<FoodResponse>> searchFood(@RequestParam String name) {
        return ResponseEntity.ok(foodService.searchFood(name));
    }
    @GetMapping("/price")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'RESTAURANT_OWNER', 'ADMIN')")
    public ResponseEntity<List<FoodResponse>> getFoodsBelowPrice(@RequestParam BigDecimal price) {
        return ResponseEntity.ok(foodService.getFoodsBelowPrice(price));
    }
    @GetMapping("/filter")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'RESTAURANT_OWNER', 'ADMIN')")
    public ResponseEntity<List<FoodResponse>> searchFoods(@RequestParam(required=false) String name,@RequestParam(required=false)FoodCategory category,@RequestParam(required=false)BigDecimal maxPrice) {
        return ResponseEntity.ok(foodService.searchFoods(name, category, maxPrice));
    }
}