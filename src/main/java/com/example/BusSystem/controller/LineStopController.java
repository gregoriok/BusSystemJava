package com.example.BusSystem.controller;

import com.example.BusSystem.domain.Line.Line;
import com.example.BusSystem.domain.Line.LineRepository;
import com.example.BusSystem.domain.LineStop.*;
import com.example.BusSystem.domain.Stop.Stop;
import com.example.BusSystem.domain.Stop.StopsRepository;
import com.example.BusSystem.service.LineStopService;
import com.example.BusSystem.utils.ApiErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/lines/{lineId}/stops")
public class LineStopController {

    @Autowired
    private LineRepository linerepository;
    @Autowired
    private StopsRepository stopRepository;
    @Autowired
    private LineStopRepository lineStopRepository;
    @Autowired
    private LineStopService lineStopService;

    @PostMapping
    public ResponseEntity<?> insertStop(@PathVariable UUID lineId,
                                                          @RequestBody @Valid LineStopDataInsert data) {
        LineStopResponseDto dto = lineStopService.addStopToLine(lineId,data);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @GetMapping
    public ResponseEntity<List<LineStopResponseDto>> listStops(@PathVariable UUID lineId) {
        List<LineStop> lineStops = lineStopRepository.findByLineIdOrderBySequenceAsc(lineId);
        List<LineStopResponseDto> dtoList = lineStops.stream()
                .map(LineStopResponseDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);
    }

    @DeleteMapping("/{stopId}")
    public ResponseEntity<?> deleteStop(@PathVariable UUID lineId, @PathVariable UUID stopId) {
        LineStopId id = new LineStopId(lineId, stopId);
        LineStop lineStopToDelete = lineStopRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("LineStop relationship not found."));

        int deletedSequence = lineStopToDelete.getSequence();

        lineStopRepository.delete(lineStopToDelete);

        List<LineStop> remainingLineStops = lineStopRepository.findByLineIdAndSequenceGreaterThan(lineId, deletedSequence);
        for (LineStop lineStop : remainingLineStops) {
            lineStop.setSequence(lineStop.getSequence() - 1);
        }
        lineStopRepository.saveAll(remainingLineStops);


        ApiErrorResponse response = new ApiErrorResponse("Record deleted successfully, and remaining stops reordered.");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
