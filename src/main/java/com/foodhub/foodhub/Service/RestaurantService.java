package com.foodhub.foodhub.Service;

import com.foodhub.foodhub.Dto.Request.RestaurantRequest;
import com.foodhub.foodhub.Dto.Response.RestaurantResponse;
import com.foodhub.foodhub.Entity.Restaurant;
import com.foodhub.foodhub.Entity.User;
import com.foodhub.foodhub.Enum.Role;
import com.foodhub.foodhub.Exception.RestaurantDoesNotExistException;
import com.foodhub.foodhub.Exception.UserDoesNotExistException;
import com.foodhub.foodhub.Mapper.RestaurantMapper;
import com.foodhub.foodhub.Repository.RestaurantRepository;
import com.foodhub.foodhub.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantMapper restaurantMapper;
    private final UserRepository userRepository;

    public RestaurantResponse createRestaurant(Authentication authentication,RestaurantRequest request) {
        String email = authentication.getName();
        User owner = userRepository.findByEmail(email).orElseThrow(()->new UserDoesNotExistException("User not found with email: " + email));
        if (owner.getRole() == Role.CUSTOMER) {
            owner.setRole(Role.RESTAURANT_OWNER);
            userRepository.save(owner);
        }
        Restaurant restaurant=restaurantMapper.toEntity(request, owner);
        restaurant.setActive(true);
        Restaurant savedRestaurant=restaurantRepository.save(restaurant);
        return restaurantMapper.toResponse(savedRestaurant);
    }
    public List<RestaurantResponse> getMyRestaurants(Authentication authentication) {
        String email = authentication.getName();
        User owner = userRepository.findByEmail(email).orElseThrow(()->new UserDoesNotExistException("User not found with email: " + email));

        return restaurantRepository.findByOwnerId(owner.getId())
                .stream()
                .map(restaurantMapper::toResponse)
                .toList();
    }


    public RestaurantResponse getRestaurantById(Long id) {
        Restaurant restaurant = restaurantRepository.findById(id).orElseThrow(() -> new RestaurantDoesNotExistException("Restaurant not found"));
        return restaurantMapper.toResponse(restaurant);
    }

    public List<RestaurantResponse> getAllRestaurants() {
        return restaurantRepository.findAll()
                .stream()
                .map(restaurantMapper::toResponse)
                .toList();
    }

    public void deleteRestaurant(Long id,Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow(()->new UserDoesNotExistException("User not found with email: "+email));
        Restaurant restaurant = restaurantRepository.findById(id).orElseThrow(()->new RestaurantDoesNotExistException("Restaurant not found"));
        if (!restaurant.getOwner().getId().equals(user.getId())) {
            throw new RuntimeException("You are not allowed to delete this restaurant");
        }
        restaurantRepository.delete(restaurant);
    }
    public RestaurantResponse toggleRestaurantStatus(Long restaurantId,Authentication authentication){
        String email = authentication.getName();
        User owner = userRepository.findByEmail(email).orElseThrow(()->new UserDoesNotExistException("User not found"));
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(()->new RestaurantDoesNotExistException("Restaurant not found"));
        if (!restaurant.getOwner().getId().equals(owner.getId())) {
            throw new RuntimeException("You are not allowed to change this restaurant status");
        }
        restaurant.setActive(!Boolean.TRUE.equals(restaurant.getActive()));
        Restaurant updatedRestaurant=restaurantRepository.save(restaurant);
        return restaurantMapper.toResponse(updatedRestaurant);
    }
}