package com.foodhub.foodhub.Controller;

import com.foodhub.foodhub.Dto.Request.DeliveryBoyRequest;
import com.foodhub.foodhub.Service.DeliveryBoyApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/delivery-boys")
@RequiredArgsConstructor
public class DeliveryBoyApplicationController {

    private final DeliveryBoyApplicationService applicationService;

    @PostMapping("/apply")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<String> apply(Authentication authentication,@Valid @RequestBody DeliveryBoyRequest request){
        applicationService.apply(authentication,request);
        return ResponseEntity.ok("Successfully applied for the job");
    }
}