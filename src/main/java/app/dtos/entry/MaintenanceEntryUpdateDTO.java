package app.dtos.entry;

import app.enums.MaintenanceCategory;

import java.time.LocalDate;

public record MaintenanceEntryUpdateDTO(LocalDate date, Float mileage, MaintenanceCategory category, String description, Double cost) {}
