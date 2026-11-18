package com.foodhub.foodhub.Service;

import com.foodhub.foodhub.Dto.Request.UserRequest;
import com.foodhub.foodhub.Dto.Response.UserResponse;
import com.foodhub.foodhub.Entity.User;
import com.foodhub.foodhub.Enum.Role;
import com.foodhub.foodhub.Exception.UserDoesNotExistException;
import com.foodhub.foodhub.Mapper.UserMapper;
import com.foodhub.foodhub.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserResponse registerUser(UserRequest request) {

        User user = userMapper.toEntity(request);

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.CUSTOMER);
        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    public UserResponse getUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserDoesNotExistException("User not found"));

        return userMapper.toResponse(user);
    }

    public List<UserResponse> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponse)
                .toList();
    }

    public void deleteUser(Long id) {

        userRepository.deleteById(id);
    }
}