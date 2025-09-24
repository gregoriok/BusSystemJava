package com.example.BusSystem.service;

import com.example.BusSystem.domain.Line.Line;
import com.example.BusSystem.domain.Line.LineRepository;
import com.example.BusSystem.domain.LineStop.*;
import com.example.BusSystem.domain.Stop.Stop;
import com.example.BusSystem.domain.Stop.StopsRepository;
import com.example.BusSystem.service.exception.InactiveLineException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class LineStopService {

    @Autowired
    private LineRepository lineRepository;
    @Autowired
    private StopsRepository stopRepository;
    @Autowired
    private LineStopRepository lineStopRepository;

    @Transactional
    public LineStopResponseDto addStopToLine(UUID lineId, LineStopDataInsert data) {
        Line line = lineRepository.findById(lineId)
                .orElseThrow(() -> new EntityNotFoundException("Line not found"));

        if (!line.getActive()) {
            throw new InactiveLineException("Cannot add stops to a disabled line.");
        }

        Stop stop = stopRepository.findById(data.stopId())
                .orElseThrow(() -> new EntityNotFoundException("Stop not found"));

        LineStopId lineStopId = new LineStopId(line.getId(), stop.getId());
        LineStop lineStop = new LineStop();
        lineStop.setId(lineStopId);
        lineStop.setLine(line);
        lineStop.setStop(stop);
        lineStop.setSequence(data.sequence());

        LineStop savedLineStop = lineStopRepository.save(lineStop);
        return new LineStopResponseDto(savedLineStop);
    }
}
