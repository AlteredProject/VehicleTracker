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
public class FuelEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer entryId;
    private LocalDate date;
    private float mileage;
    private double liters;
    private double price;

    @ManyToOne
    @ToString.Exclude
    private Vehicle vehicle;
}
