package app.entities;

import jakarta.persistence.*;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer entryId;
    private LocalDate date;
    private float mileage;
    private String description;
    private double cost;

    @ManyToOne
    @ToString.Exclude
    private Vehicle vehicle;
}
