package app.dtos.statistics;

import java.time.LocalDate;

public record MileageDataPointDTO(LocalDate date, double mileage) {}
