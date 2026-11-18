package com.foodhub.foodhub.Service;

import com.foodhub.foodhub.Dto.Request.DeliveryBoyRequest;
import com.foodhub.foodhub.Entity.DeliveryBoyApplication;
import com.foodhub.foodhub.Entity.User;
import com.foodhub.foodhub.Enum.DeliveryBoyStatus;
import com.foodhub.foodhub.Enum.Role;
import com.foodhub.foodhub.Mapper.DeliveryBoyApplicationMapper;
import com.foodhub.foodhub.Repository.DeliveryBoyApplicationRepository;
import com.foodhub.foodhub.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeliveryBoyApplicationService {

    private final DeliveryBoyApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final DeliveryBoyApplicationMapper applicationMapper;

    public void apply(Authentication authentication,DeliveryBoyRequest request){
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        if (applicationRepository.existsById(user.getId())) {
            throw new RuntimeException("Application already exists");
        }
        DeliveryBoyApplication application=applicationMapper.toEntity(request, user);
        applicationRepository.save(application);
    }

    public List<DeliveryBoyApplication> getPendingApplications() {
        return applicationRepository.findByStatus(
                DeliveryBoyStatus.PENDING
        );
    }
    @Transactional
    public void approveApplication(Long userId) {
        DeliveryBoyApplication application=applicationRepository.findById(userId).orElseThrow(()->new RuntimeException("Application not found"));
        if (application.getStatus() != DeliveryBoyStatus.PENDING) {
            throw new RuntimeException("Application has already been processed");
        }
        application.setStatus(DeliveryBoyStatus.APPROVED);
        application.setAvailable(false);
        User user = application.getUser();
        user.setRole(Role.DELIVERY_BOY);
        userRepository.save(user);
        applicationRepository.save(application);
    }
    public void rejectApplication(Long userId) {
        DeliveryBoyApplication application=applicationRepository.findById(userId).orElseThrow(()->new RuntimeException("Application not found"));
        if (application.getStatus() != DeliveryBoyStatus.PENDING){
            throw new RuntimeException("Application has already been processed");
        }
        application.setStatus(DeliveryBoyStatus.REJECTED);
        application.setAvailable(false);
        applicationRepository.save(application);
    }
    public boolean toggleAvailability(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        DeliveryBoyApplication application = applicationRepository.findById(user.getId()).orElseThrow(()->new RuntimeException("Delivery boy application not found"));
        if (application.getStatus() != DeliveryBoyStatus.APPROVED) {
            throw new RuntimeException("You are not an approved delivery boy");
        }
        application.setAvailable(!application.getAvailable());
        applicationRepository.save(application);
        return application.getAvailable();
    }
}