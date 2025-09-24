package com.example.BusSystem.service;

import com.example.BusSystem.domain.Bus.Bus;
import com.example.BusSystem.domain.Bus.BusDataUpdate;
import com.example.BusSystem.domain.Line.Line;
import com.example.BusSystem.domain.Line.LineDataInsert;
import com.example.BusSystem.domain.Line.LineDataUpdate;
import com.example.BusSystem.domain.Line.LineRepository;
import com.example.BusSystem.domain.LineStop.LineStopRepository;
import com.example.BusSystem.domain.Stop.StopsRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class LineService {

    @Autowired
    private LineRepository repository;
    @Autowired
    private StopsRepository stopRepository;
    @Autowired
    private LineStopRepository lineStopRepository;

    public Line create(LineDataInsert data){
        Line line = new Line(data);
        return repository.save(line);
    }

    public Page<Line> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Line getOne(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException());
    }

    public Line update(UUID id, LineDataUpdate data) {
        var line = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException());
        line.updateLine(data);
        return repository.save(line);
    }

    public void delete(UUID id) {
        var line = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException());
        repository.delete(line);
    }
}
