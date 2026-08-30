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
public class Vehicle {
    @Id
    private Long vehicleId;
    private String make;
    private String model;
    private int modelYear;
    private String registrationNumber;
    private float mileage;

    private LocalDate createdAt;
    private LocalDate updatedAt;
}
