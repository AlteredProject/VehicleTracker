package app.dtos.statistics;

import java.time.LocalDate;

public record ExpenseDataPointDTO(LocalDate date, double fuelExpenses, double maintenanceExpenses) {}
