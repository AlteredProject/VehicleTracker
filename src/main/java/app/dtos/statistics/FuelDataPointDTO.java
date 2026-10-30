package app.dtos.statistics;

import java.time.LocalDate;

public record FuelDataPointDTO(LocalDate date, double litres, double pricePerLitre, double totalPrice, float mileage) {}