package app.entities;

import app.enums.MileageEntrySource;
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
public class MileageEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long entryId;
    private LocalDate date;
    private float mileage;

    @Enumerated(EnumType.STRING)
    private MileageEntrySource source;

    private LocalDate createdAt;
    private LocalDate updatedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vehicle_id", nullable = false)
    @ToString.Exclude
    private Vehicle vehicle;

    @OneToOne
    @JoinColumn(name = "fuel_entry_id", unique = true)
    private FuelEntry fuelEntry;

    @OneToOne
    @JoinColumn(name = "maintenance_entry_id", unique = true)
    private MaintenanceEntry maintenanceEntry;
}
