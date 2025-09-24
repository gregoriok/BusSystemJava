package com.example.BusSystem.domain.Stop;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface StopsRepository extends JpaRepository<Stop, UUID> {
}
