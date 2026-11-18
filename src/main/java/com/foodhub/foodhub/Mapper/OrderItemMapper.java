
package com.foodhub.foodhub.Mapper;

import com.foodhub.foodhub.Dto.Response.OrderItemResponse;
import com.foodhub.foodhub.Entity.OrderItem;

import java.math.BigDecimal;

public class OrderItemMapper {

    public static OrderItemResponse toResponse(OrderItem item) {

        BigDecimal subtotal = item.getPrice()
                .multiply(BigDecimal.valueOf(item.getQuantity()));

        return OrderItemResponse.builder()
                .id(item.getId())
                .foodId(item.getFood().getId())
                .foodName(item.getFood().getName())
                .quantity(item.getQuantity())
                .price(item.getPrice())
                .subtotal(subtotal)
                .build();
    }
}
