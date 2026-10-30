package app.entities;

import app.enums.MaintenanceCategory;
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
    private Long entryId;
    private LocalDate date;
    private float mileage;

    @Enumerated(EnumType.STRING)
    private MaintenanceCategory category;

    @Column(length = 255)
    private String description;

    private double cost;

    private LocalDate createdAt;
    private LocalDate updatedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vehicle_id", nullable = false)
    @ToString.Exclude
    private Vehicle vehicle;

    @OneToOne(mappedBy = "maintenanceEntry")
    private MileageEntry mileageEntry;
}
