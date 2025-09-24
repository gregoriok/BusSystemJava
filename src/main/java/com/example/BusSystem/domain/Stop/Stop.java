package com.example.BusSystem.domain.Stop;

import com.example.BusSystem.domain.Bus.BusDataUpdate;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Table(name = "stops")
@Entity(name = "stops")
@Data
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Stop {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String name;
    private Double latitude;
    private Double longitude;
    private String description;


    public Stop(StopDataInsert data) {
        this.name = data.name();
        this.latitude = data.latitude();
        this.longitude = data.longitude();
        this.description = data.description();
    }

    public void updateStop(StopDataUpdate data){
        if (data.name() != null) {
            this.name = data.name();
        }
        if (data.latitude() != null){
            this.latitude = data.latitude();
        }
        if(data.longitude() != null){
            this.longitude = data.longitude();
        }
        if (data.description()!= null){
            this.description = data.description();
        }
    }
}
