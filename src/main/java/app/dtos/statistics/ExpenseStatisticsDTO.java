package app.dtos.statistics;

import java.util.List;

public record ExpenseStatisticsDTO(
        double totalExpenses,
        double fuelExpenses,
        double maintenanceExpenses,
        Double averageMonthlyExpenses,
        Double fuelCostPerKm,
        Double maintenanceCostPerKm,
        Double totalCostPerKm,
        List<ExpenseDataPointDTO> dataPoints
) {}
