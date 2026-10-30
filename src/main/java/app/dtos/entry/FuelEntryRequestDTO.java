package app.dtos.entry;

import java.time.LocalDate;

public record FuelEntryRequestDTO (LocalDate date, float mileage, double liters, double price) {}
