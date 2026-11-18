package com.foodhub.foodhub.Mapper;

import com.foodhub.foodhub.Dto.Request.UserRequest;
import com.foodhub.foodhub.Dto.Response.UserResponse;
import com.foodhub.foodhub.Entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(UserRequest request) {
        return User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(request.getPassword())
                .build();
    }

    public UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}