package com.plazza.ridehailing.service;

import com.plazza.ridehailing.dto.request.RegisterUserRequest;
import com.plazza.ridehailing.dto.response.UserResponse;
import com.plazza.ridehailing.exception.ConflictException;
import com.plazza.ridehailing.exception.ResourceNotFoundException;
import com.plazza.ridehailing.model.Location;
import com.plazza.ridehailing.model.User;
import com.plazza.ridehailing.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse registerUser(RegisterUserRequest request) {
        if (userRepository.findByPhone(request.getPhone()).isPresent()) {
            throw new ConflictException("User already registered with phone: " + request.getPhone());
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setCurrentLocation(request.getLocation() != null ? request.getLocation() : new Location(0.0, 0.0));

        User saved = userRepository.save(user);
        return UserResponse.from(saved);
    }

    public UserResponse getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return UserResponse.from(user);
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserResponse::from)
                .toList();
    }

    public UserResponse updateUserLocation(String id, Location location) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setCurrentLocation(location);
        userRepository.save(user);
        return UserResponse.from(user);
    }
}
