package com.example.BusSystem.domain.LineStop;

import com.example.BusSystem.domain.Line.Line;
import com.example.BusSystem.domain.Stop.Stop;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "line_stops")
@Data
@NoArgsConstructor
public class LineStop {

    @EmbeddedId
    private LineStopId id;

    private int sequence;

    // Relacionamentos com as entidades
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("lineId") // Mapeia o campo "lineId" da chave composta
    @JoinColumn(name = "line_id")
    private Line line;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("stopId") // Mapeia o campo "stopId" da chave composta

    @JoinColumn(name = "stop_id")
    private Stop stop;
}
