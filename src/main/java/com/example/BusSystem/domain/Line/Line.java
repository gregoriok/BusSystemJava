package com.example.BusSystem.domain.Line;
import com.example.BusSystem.domain.Bus.BusDataUpdate;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Table(name = "lines")
@Entity(name = "lines")
@Data
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Line {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String name;
    private String description;
    private Boolean active;

    public Line(LineDataInsert data) {
        this.name = data.name();
        this.description = data.description();
        this.active = data.active();
    }

    public void updateLine(LineDataUpdate data){
        if (data.name() != null) {
            this.name = data.name();
        }
        if (data.description() != null){
            this.description = data.description();
        }
        if(data.active() != null){
            this.active = data.active();
        }
    }

}
