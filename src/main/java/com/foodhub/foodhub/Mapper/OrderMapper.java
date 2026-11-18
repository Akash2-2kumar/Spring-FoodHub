package com.foodhub.foodhub.Mapper;

import com.foodhub.foodhub.Dto.Response.OrderItemResponse;
import com.foodhub.foodhub.Dto.Response.OrderResponse;
import com.foodhub.foodhub.Entity.Order;
import com.foodhub.foodhub.Entity.OrderItem;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class OrderMapper {

    public OrderResponse toResponse(Order order,List<OrderItemResponse> itemResponses){
        Long deliveryBoyId = null;
        if (order.getDeliveryBoy()!=null){
            deliveryBoyId = order.getDeliveryBoy().getId();
        }
        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .deliveryBoyId(deliveryBoyId)
                .totalAmount(order.getTotalAmount())
                .orderDate(order.getOrderDate())
                .status(order.getStatus())
                .items(itemResponses)
                .build();
    }

    public OrderItemResponse toItemResponse(OrderItem orderItem) {

        BigDecimal subtotal = orderItem.getPrice()
                .multiply(BigDecimal.valueOf(orderItem.getQuantity()));

        return OrderItemResponse.builder()
                .id(orderItem.getId())
                .foodId(orderItem.getFood().getId())
                .foodName(orderItem.getFood().getName())
                .quantity(orderItem.getQuantity())
                .price(orderItem.getPrice())
                .subtotal(subtotal)
                .build();
    }
}