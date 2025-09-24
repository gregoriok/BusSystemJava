package com.example.BusSystem.service;

import com.example.BusSystem.domain.User.User;
import com.example.BusSystem.domain.User.UserDataInsert;
import com.example.BusSystem.domain.User.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;

    public User create(UserDataInsert data){
        String encodedPassword = passwordEncoder.encode(data.password());
        User user = new User(data);
        user.setPassword(encodedPassword);
        return userRepository.save(user);
    }
    public User getUserById(UUID userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }
}
