package com.example.BusSystem.domain.Bus;

import com.example.BusSystem.domain.Line.Line;
import com.example.BusSystem.domain.Stop.Stop;
import com.example.BusSystem.domain.Bus.BusDataInsert;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Table(name = "bus")
@Entity(name = "bus")
@Data
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Bus {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private Integer number;
    private String licensePlate;

    private Integer capacity;

    @Enumerated(EnumType.STRING)
    private BusTypeEnum type;

    private Double currentLatitude;

    private Double currentLongitude;

    private LocalDateTime lastLocationUpdate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_line_id")
    private Line currentLine;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "next_stop_id")
    private Stop nextStop;

    public Bus(BusDataInsert data) {
        this.licensePlate = data.licensePlate();
        this.capacity = data.capacity();
        this.type = data.type();
        this.number = data.number();
    }

    public void updateBus(BusDataUpdate data){
        if (data.licensePlate() != null) {
            this.setLicensePlate(data.licensePlate());
        }
        if (data.capacity() != null){
            this.setCapacity(data.capacity());
        }
        if(data.type() != null){
            this.setType(data.type());
        }
        if (data.number()!= null){
            this.setNumber(data.number());
        }
    }

    public void updateLocalization(BusLocalizationUpdate data) {
        this.currentLatitude = data.currentLatitude();
        this.currentLongitude = data.currentLongitude();
        this.lastLocationUpdate = LocalDateTime.now();
    }
}