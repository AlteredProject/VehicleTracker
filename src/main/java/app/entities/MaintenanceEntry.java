package app.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class MaintenanceEntry {
    @Id
    private long entryId;
    private LocalDate date;
    private float mileage;
    private String description;
    private double cost;

}
