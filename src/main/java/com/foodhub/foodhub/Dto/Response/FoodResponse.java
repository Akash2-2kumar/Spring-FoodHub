package com.foodhub.foodhub.Dto.Response;

import com.foodhub.foodhub.Enum.FoodCategory;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodResponse {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private FoodCategory category;
    private Long restaurantId;
}