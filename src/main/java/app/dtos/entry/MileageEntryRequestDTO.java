package app.dtos.entry;

import app.enums.MileageEntrySource;

import java.time.LocalDate;

public record MileageEntryRequestDTO(LocalDate date, float mileage, MileageEntrySource source) {
}
