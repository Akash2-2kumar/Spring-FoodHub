package com.foodhub.foodhub.Controller;

import com.foodhub.foodhub.Dto.Request.RestaurantRequest;
import com.foodhub.foodhub.Dto.Response.RestaurantResponse;
import com.foodhub.foodhub.Service.RestaurantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;

    @PostMapping("/createRestaurant")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'RESTAURANT_OWNER')")
    public ResponseEntity<RestaurantResponse> createRestaurant(Authentication authentication, @Valid @RequestBody RestaurantRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(restaurantService.createRestaurant(authentication,request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'RESTAURANT_OWNER', 'ADMIN')")
    public ResponseEntity<RestaurantResponse> getRestaurantById(@PathVariable Long id) {
        return ResponseEntity.ok(restaurantService.getRestaurantById(id));
    }

    @GetMapping("/getRestaurants")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'RESTAURANT_OWNER', 'ADMIN')")
    public ResponseEntity<List<RestaurantResponse>> getAllRestaurants() {

        return ResponseEntity.ok(restaurantService.getAllRestaurants());
    }

    @DeleteMapping("/deleteById/{id}")
    @PreAuthorize("hasAnyRole('RESTAURANT_OWNER', 'ADMIN')")
    public ResponseEntity<Void> deleteRestaurant(@PathVariable Long id,Authentication authentication) {
        restaurantService.deleteRestaurant(id,authentication);
        return ResponseEntity.noContent().build();
    }
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<RestaurantResponse> toggleRestaurantStatus(@PathVariable Long id,Authentication authentication){
        return ResponseEntity.ok(restaurantService.toggleRestaurantStatus(id,authentication));
    }
}