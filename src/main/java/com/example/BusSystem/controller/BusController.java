package com.example.BusSystem.controller;

import com.example.BusSystem.domain.Bus.*;
import com.example.BusSystem.service.BusService;
import com.example.BusSystem.utils.ApiErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/bus")
public class BusController {

    @Autowired
    private BusService busService;

    //O @Valid é para buscar nas exceptions do RestControllerAdvice
    @PostMapping
    public ResponseEntity<BusResponseDto> create(@RequestBody @Valid BusDataInsert data) {
        var bus = busService.create(data);
        return ResponseEntity.status(HttpStatus.CREATED).body(new BusResponseDto(bus));
    }

    @GetMapping
    public ResponseEntity<Page<BusResponseDto>> list(Pageable pageable){
        var page = busService.list(pageable).map(BusResponseDto::new);
        return ResponseEntity.status(HttpStatus.OK).body(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BusResponseDto> getOne(@PathVariable @Valid UUID id){
        var bus = busService.getOne(id);
        return ResponseEntity.status(HttpStatus.OK).body(new BusResponseDto(bus));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BusResponseDto> update(@PathVariable @Valid UUID id, @RequestBody @Valid BusDataUpdate data){
        var bus = busService.update(id, data);
        return ResponseEntity.status(HttpStatus.OK).body(new BusResponseDto(bus));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable @Valid UUID id){
        busService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/localization")
    public ResponseEntity<Void> updateLocation(@PathVariable UUID id, @RequestBody BusLocalizationUpdate data) {
        busService.updateBusLocation(id, data);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/localization")
    public ResponseEntity<BusLocalizationResponseDto> getLocation(@PathVariable UUID id) {
        Map<String, Object> locationData = busService.getBusLocation(id)
                .orElseThrow(() -> new EntityNotFoundException("Location data not found."));

        var dto = new BusLocalizationResponseDto(
                (Double) locationData.get("latitude"),
                (Double) locationData.get("longitude")
        );
        return ResponseEntity.ok(dto);
    }


}
