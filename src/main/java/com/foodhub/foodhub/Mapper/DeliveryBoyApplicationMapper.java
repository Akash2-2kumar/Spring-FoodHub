package com.foodhub.foodhub.Mapper;

import com.foodhub.foodhub.Dto.Request.DeliveryBoyRequest;
import com.foodhub.foodhub.Entity.DeliveryBoyApplication;
import com.foodhub.foodhub.Entity.User;
import com.foodhub.foodhub.Enum.DeliveryBoyStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DeliveryBoyApplicationMapper {

    public DeliveryBoyApplication toEntity(DeliveryBoyRequest request, User user) {

        return DeliveryBoyApplication.builder()
                .user(user)
                .age(request.getAge())
                .address(request.getAddress())
                .phone(request.getPhone())
                .vehicleType(request.getVehicleType())
                .vehicleNumber(request.getVehicleNumber())
                .drivingLicenseNumber(request.getDrivingLicenseNumber())
                .status(DeliveryBoyStatus.PENDING)
                .available(false)
                .appliedAt(LocalDateTime.now())
                .build();
    }
}
