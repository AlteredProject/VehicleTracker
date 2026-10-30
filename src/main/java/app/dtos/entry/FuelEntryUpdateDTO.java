package app.dtos.entry;

import java.time.LocalDate;

public record FuelEntryUpdateDTO(LocalDate date, Float mileage, Double liters, Double price) {}