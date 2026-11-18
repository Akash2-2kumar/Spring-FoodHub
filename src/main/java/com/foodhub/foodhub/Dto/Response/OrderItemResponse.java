package com.foodhub.foodhub.Dto.Response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemResponse {

    private Long id;
    private Long foodId;
    private String foodName;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal subtotal;
}