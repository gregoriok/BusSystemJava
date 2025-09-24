package com.example.BusSystem.controller;

import com.example.BusSystem.domain.Line.*;
import com.example.BusSystem.domain.LineStop.*;
import com.example.BusSystem.service.LineService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/lines")
public class LinesController {

    @Autowired
    private LineService lineService;

    @PostMapping
    public ResponseEntity<LineResponseDto> create(@RequestBody LineDataInsert data, UriComponentsBuilder uriBuilder) {
        Line line = lineService.create(data);
        return ResponseEntity.status(HttpStatus.CREATED).body(new LineResponseDto(line));
    }

    @GetMapping
    public ResponseEntity<Page<LineResponseDto>> list(Pageable pageable){
        var page = lineService.list(pageable).map(LineResponseDto::new);
        return ResponseEntity.status(HttpStatus.OK).body(page);
    }

    @GetMapping("/{Id}")
    public ResponseEntity<LineResponseDto> getOne(@PathVariable @Valid UUID Id){
        var line = lineService.getOne(Id);
        return ResponseEntity.status(HttpStatus.OK).body(new LineResponseDto(line));
    }

    @PutMapping("/{Id}")
    public ResponseEntity<LineResponseDto> update(@PathVariable UUID Id, @RequestBody @Valid LineDataUpdate data){
        var line = lineService.update(Id,data);
        return ResponseEntity.status(HttpStatus.OK).body(new LineResponseDto(line));
    }

    @DeleteMapping("/{Id}")
    public ResponseEntity<?> delete(@PathVariable @Valid UUID Id){
        lineService.delete(Id);
        return ResponseEntity.noContent().build();
    }

}
