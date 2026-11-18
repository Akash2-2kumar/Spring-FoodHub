package com.foodhub.foodhub.Controller;

import com.foodhub.foodhub.Dto.Request.OrderRequest;
import com.foodhub.foodhub.Dto.Response.OrderResponse;
import com.foodhub.foodhub.Service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    @PostMapping("/placeOrder")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<OrderResponse> placeOrder(Authentication authentication, @Valid @RequestBody OrderRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(orderService.placeOrder(authentication, request));
    }
    @GetMapping("/{orderId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long orderId,Authentication authentication) {
        return ResponseEntity.ok(orderService.getOrderById(orderId,authentication));
    }
    @GetMapping("/user/getOrders")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<List<OrderResponse>> getUserOrders(Authentication authentication) {
        return ResponseEntity.ok(orderService.getUserOrders(authentication));
    }
    @PreAuthorize("hasRole('CUSTOMER')")
    @PutMapping("/cancel/{orderId}")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable Long orderId, Authentication authentication){
        return ResponseEntity.ok(orderService.cancelOrder(orderId,authentication));
    }
    @PutMapping("/confirm/{orderId}")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<OrderResponse> confirmOrder(@PathVariable Long orderId, Authentication authentication){
        return ResponseEntity.ok(orderService.confirmOrder(orderId,authentication));
    }
    @GetMapping("/restaurant/orders")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<List<OrderResponse>> getRestaurantOrders(Authentication authentication) {
        return ResponseEntity.ok(orderService.getRestaurantOrders(authentication));
    }
}