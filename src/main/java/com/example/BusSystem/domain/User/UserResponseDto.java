package com.example.BusSystem.domain.User;

import com.example.BusSystem.domain.Bus.Bus;

import java.util.UUID;

public record UserResponseDto(UUID id) {
    public UserResponseDto(User user){
        this(user.getId());
    }
}
