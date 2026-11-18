package com.foodhub.foodhub.Dto.Response;

import com.foodhub.foodhub.Enum.OrderStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {

    private Long id;
    private Long userId;
    private BigDecimal totalAmount;
    private Long deliveryBoyId;
    private LocalDateTime orderDate;
    private OrderStatus status;
    private List<OrderItemResponse> items;
}