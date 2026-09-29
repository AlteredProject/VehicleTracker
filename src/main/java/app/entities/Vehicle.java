package app.entities;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long vehicleId;
    private String registrationNumber;
    private String status;
    private String vin;
    private String type;
    private String make;
    private String model;
    private String variant;
    private String chassisType;
    private int modelYear;
    private String color;
    private String fuelType;
    private Double engineVolume;
    private Double enginePower;
    private float mileage;

    private LocalDate createdAt;
    private LocalDate updatedAt;


    @ManyToOne
    @ToString.Exclude
    private User user;


    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL)
    @Builder.Default
    @ToString.Exclude
    private Set<FuelEntry> fuelEntries = new HashSet<>();

    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL)
    @Builder.Default
    @ToString.Exclude
    private Set<MaintenanceEntry> maintenanceEntries = new HashSet<>();

    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL)
    @Builder.Default
    @ToString.Exclude
    private Set<MileageEntry> mileageEntries = new HashSet<>();


    public void addFuelEntry(FuelEntry fuelEntry) {
        this.fuelEntries.add(fuelEntry);
        if (fuelEntry != null) {
            fuelEntry.setVehicle(this);
        }
    }

    public void addMaintenanceEntry(MaintenanceEntry maintenanceEntry) {
        this.maintenanceEntries.add(maintenanceEntry);
        if (maintenanceEntry != null) {
            maintenanceEntry.setVehicle(this);
        }
    }

    public void addMileageEntry(MileageEntry mileageEntry) {
        this.mileageEntries.add(mileageEntry);
        if (mileageEntry != null) {
            mileageEntry.setVehicle(this);
        }
    }

}
