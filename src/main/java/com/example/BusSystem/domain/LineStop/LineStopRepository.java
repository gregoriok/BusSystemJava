package com.example.BusSystem.domain.LineStop;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;


public interface LineStopRepository extends JpaRepository<LineStop, LineStopId> {
    List<LineStop> findByLineIdOrderBySequenceAsc(UUID lineId);

    List<LineStop> findByLineIdAndSequenceGreaterThan(UUID lineId, int sequence);
}
