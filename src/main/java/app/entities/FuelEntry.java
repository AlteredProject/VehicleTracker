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
public class FuelEntry {
    @Id
    private long entryId;
    private LocalDate date;
    private float mileage;
    private double liters;
    private double price;
}
