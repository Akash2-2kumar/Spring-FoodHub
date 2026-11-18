package com.foodhub.foodhub.Controller;

import com.foodhub.foodhub.Dto.Response.OrderResponse;
import com.foodhub.foodhub.Entity.DeliveryBoyApplication;
import com.foodhub.foodhub.Service.DeliveryBoyApplicationService;
import com.foodhub.foodhub.Service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final DeliveryBoyApplicationService applicationService;
    private final OrderService orderService;

    @GetMapping("/delivery-boys/pending")
    public ResponseEntity<List<DeliveryBoyApplication>> getPendingApplications() {
        return ResponseEntity.ok(applicationService.getPendingApplications());
    }

    @PutMapping("/delivery-boys/{userId}/approve")
    public ResponseEntity<String> approveApplication(@PathVariable Long userId){
        applicationService.approveApplication(userId);
        return ResponseEntity.ok("Delivery boy application approved successfully"
        );
    }

    @PutMapping("/delivery-boys/{userId}/reject")
    public ResponseEntity<String> rejectApplication(@PathVariable Long userId){
        applicationService.rejectApplication(userId);
        return ResponseEntity.ok("Delivery boy application rejected successfully");
    }
    @PutMapping("/orders/{orderId}/assign-delivery-boy/{deliveryBoyId}")
    public ResponseEntity<OrderResponse> assignDeliveryBoy(@PathVariable Long orderId,@PathVariable Long deliveryBoyId) {
        return ResponseEntity.ok(orderService.assignDeliveryBoy(orderId,deliveryBoyId));
    }

}