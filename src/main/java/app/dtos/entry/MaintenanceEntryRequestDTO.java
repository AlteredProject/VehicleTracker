package app.dtos.entry;

import app.enums.MaintenanceCategory;

import java.time.LocalDate;

public record MaintenanceEntryRequestDTO(LocalDate date, float mileage, MaintenanceCategory category, String description, double cost){
}
