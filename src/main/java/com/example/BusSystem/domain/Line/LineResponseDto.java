package com.example.BusSystem.domain.Line;


import java.util.UUID;

public record LineResponseDto(UUID id,String name,String description,Boolean active) {
    public LineResponseDto(Line line){
        this(line.getId(),line.getName(),line.getDescription(),line.getActive());
    }
}
