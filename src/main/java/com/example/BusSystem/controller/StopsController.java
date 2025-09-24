package com.example.BusSystem.controller;

import com.example.BusSystem.domain.Stop.*;
import com.example.BusSystem.utils.ApiErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/stops")
public class StopsController {

    @Autowired
    private StopsRepository repository;

    @PostMapping
    public ResponseEntity<StopsResponseDto> create(@RequestBody StopDataInsert data) {
        Stop stop = new Stop(data);
        Stop savedstop = repository.save(stop);
        return ResponseEntity.status(HttpStatus.CREATED).body(new StopsResponseDto(savedstop));
    }

    @GetMapping
    public ResponseEntity<Page<StopsResponseDto>> list(Pageable pageable){
        var page = repository.findAll(pageable).map(StopsResponseDto::new);
        return ResponseEntity.status(HttpStatus.OK).body(page);
    }

    @GetMapping("/{Id}")
    public ResponseEntity<StopsResponseDto> getOne(@PathVariable @Valid UUID Id){
        var stop = repository.findById(Id)
                .orElseThrow(() -> new EntityNotFoundException());
        var stopDto = new StopsResponseDto(stop);
        return ResponseEntity.status(HttpStatus.OK).body(stopDto);
    }

    @PutMapping("/{Id}")
    public ResponseEntity<StopsResponseDto> update(@PathVariable @Valid UUID Id, @RequestBody @Valid StopDataUpdate data){
        var stop = repository.findById(Id)
                .orElseThrow(() -> new EntityNotFoundException());
        stop.updateStop(data);
        repository.save(stop);
        var stopDto = new StopsResponseDto(stop);
        return ResponseEntity.status(HttpStatus.OK).body(stopDto);
    }

    @DeleteMapping("/{Id}")
    public ResponseEntity<?> delete(@PathVariable @Valid UUID Id){
        var line = repository.findById(Id)
                .orElseThrow(() -> new EntityNotFoundException());
        repository.deleteById(Id);
        ApiErrorResponse response = new ApiErrorResponse("Record deleted successfuly");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
