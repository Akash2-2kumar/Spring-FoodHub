package com.foodhub.foodhub.Config;

import com.foodhub.foodhub.Entity.User;
import com.foodhub.foodhub.Enum.Role;
import com.foodhub.foodhub.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Override
    public void run(String... args) {
        if (userRepository.findByEmail("admin@foodhub.com").isEmpty()) {
            User admin = User.builder()
                    .name("FoodHub Admin")
                    .email("admin@foodhub.com")
                    .password(passwordEncoder.encode("Admin@123"))
                    .role(Role.ADMIN)
                    .build();
            userRepository.save(admin);
            System.out.println("Default admin created successfully.");
        }
    }
}