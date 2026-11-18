package com.foodhub.foodhub.Controller;

import com.foodhub.foodhub.Dto.Response.OrderResponse;
import com.foodhub.foodhub.Service.DeliveryBoyApplicationService;
import com.foodhub.foodhub.Service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/delivery-boys")
@RequiredArgsConstructor
public class DeliveryBoyController {

    private final DeliveryBoyApplicationService applicationService;
    private final OrderService orderService;

    @PatchMapping("/availability")
    @PreAuthorize("hasRole('DELIVERY_BOY')")
    public ResponseEntity<String> toggleAvailability(Authentication authentication) {
        boolean available=applicationService.toggleAvailability(authentication);
        String message = available?"You are now available for delivery":"You are now unavailable for delivery";
        return ResponseEntity.ok(message);
    }
    @GetMapping("/orders")
    @PreAuthorize("hasRole('DELIVERY_BOY')")
    public ResponseEntity<List<OrderResponse>> getMyOrders(Authentication authentication){

        return ResponseEntity.ok(orderService.getDeliveryBoyOrders(authentication));
    }
    @PutMapping("/orders/{orderId}/delivered")
    @PreAuthorize("hasRole('DELIVERY_BOY')")
    public ResponseEntity<OrderResponse> markOrderDelivered(@PathVariable Long orderId,Authentication authentication){
        return ResponseEntity.ok(orderService.markOrderDelivered(orderId,authentication));
    }
}