package com.foodhub.foodhub.Service;

import com.foodhub.foodhub.Dto.Request.FoodRequest;
import com.foodhub.foodhub.Dto.Response.FoodResponse;
import com.foodhub.foodhub.Entity.Food;
import com.foodhub.foodhub.Entity.Restaurant;
import com.foodhub.foodhub.Entity.User;
import com.foodhub.foodhub.Enum.FoodCategory;
import com.foodhub.foodhub.Exception.FoodDoesNotExistException;
import com.foodhub.foodhub.Exception.RestaurantDoesNotExistException;
import com.foodhub.foodhub.Exception.UserDoesNotExistException;
import com.foodhub.foodhub.Mapper.FoodMapper;
import com.foodhub.foodhub.Repository.FoodRepository;
import com.foodhub.foodhub.Repository.RestaurantRepository;
import com.foodhub.foodhub.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FoodService {

    private final FoodRepository foodRepository;
    private final RestaurantRepository restaurantRepository;

    private final UserRepository userRepository;
    private final FoodMapper foodMapper;

    public FoodResponse createFood(FoodRequest request, Authentication authentication) {
        String email = authentication.getName();

        User owner = userRepository.findByEmail(email).orElseThrow(()->new UserDoesNotExistException("User not found"));
        log.info("Creating food: {}", request.getName());
        Restaurant restaurant = restaurantRepository.findById(request.getRestaurantId()).orElseThrow(() -> {log.error("Restaurant not found with id: {}",request.getRestaurantId());
                    return new RestaurantDoesNotExistException("Restaurant not found with id: "+request.getRestaurantId());});
        if (!restaurant.getOwner().getId().equals(owner.getId())) {
            throw new RuntimeException("You are not allowed to add food to this restaurant");
        }
        Food food = foodMapper.toEntity(request, restaurant);
        Food savedFood = foodRepository.save(food);
        return foodMapper.toResponse(savedFood);
    }
    public List<FoodResponse> searchFoods(String name,FoodCategory category,BigDecimal maxPrice){
        log.info("Searching foods with name={}, category={}, maxPrice={}",name,category,maxPrice);
        return foodRepository.searchFoods(name,category,maxPrice)
                .stream()
                .map(foodMapper::toResponse)
                .toList();
    }
    public List<FoodResponse> getFoodsBelowPrice(BigDecimal price) {
        log.info("Fetching foods below price: {}", price);
        return foodRepository.findFoodBelowPrice(price)
                .stream()
                .map(foodMapper::toResponse)
                .toList();
    }
    public List<FoodResponse> searchFood(String name) {
        log.info("Searching food with name: {}", name);
        return foodRepository
                .findByNameContainingIgnoreCase(name)
                .stream()
                .map(foodMapper::toResponse)
                .toList();
    }
    public FoodResponse getFoodById(Long id) {
        Food food = foodRepository.findById(id).orElseThrow(() -> new FoodDoesNotExistException("Food not found"));
        return foodMapper.toResponse(food);
    }

    public List<FoodResponse> getAllFoods() {
        return foodRepository.findAll()
                .stream()
                .map(foodMapper::toResponse)
                .toList();
    }

    public List<FoodResponse> getFoodsByRestaurant(Long restaurantId) {
        return foodRepository.findByRestaurantId(restaurantId)
                .stream()
                .map(foodMapper::toResponse)
                .toList();
    }

    public void deleteFood(Long id,Authentication authentication){
        String email = authentication.getName();
        User owner = userRepository.findByEmail(email).orElseThrow(()->new UserDoesNotExistException("User not found"));
        Food food = foodRepository.findById(id).orElseThrow(()->new FoodDoesNotExistException("Food not found"));
        if (!food.getRestaurant().getOwner().getId().equals(owner.getId())) {
            throw new RuntimeException("You are not allowed to delete this food");
        }
        foodRepository.delete(food);
    }
    public FoodResponse updateFood(Long foodId,FoodRequest request,Authentication authentication){
        String email = authentication.getName();
        User owner=userRepository.findByEmail(email).orElseThrow(()->new UserDoesNotExistException("User not found"));
        Food food=foodRepository.findById(foodId).orElseThrow(()->new FoodDoesNotExistException("Food not found"));
        if (!food.getRestaurant().getOwner().getId().equals(owner.getId())) {
            throw new RuntimeException("You are not allowed to update this food");
        }
        food.setName(request.getName());
        food.setDescription(request.getDescription());
        food.setPrice(request.getPrice());
        food.setStock(request.getStock());
        food.setCategory(request.getCategory());
        Food updatedFood = foodRepository.save(food);

        return foodMapper.toResponse(updatedFood);
    }
}