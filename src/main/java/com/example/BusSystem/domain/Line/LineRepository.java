package com.example.BusSystem.domain.Line;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface LineRepository extends JpaRepository<Line, UUID> {
}
