package com.foodhub.foodhub.Entity;

import com.foodhub.foodhub.Enum.DeliveryBoyStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "delivery_boy_applications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryBoyApplication {

    @Id
    private Long userId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    @Min(value = 18,message = "Age must be at least 18")
    private Integer age;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String vehicleType;

    @Column(nullable = false, unique = true)
    @Size(max = 8,min = 8)
    private String vehicleNumber;

    @Column(nullable = false, unique = true)
    private String drivingLicenseNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryBoyStatus status;

    @Column(nullable = false)
    private Boolean available;

    @Column(nullable = false)
    private LocalDateTime appliedAt;
}