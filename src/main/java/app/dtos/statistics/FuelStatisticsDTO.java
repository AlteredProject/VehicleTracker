package app.dtos.statistics;

import java.util.List;

public record FuelStatisticsDTO(
        double totalLitres,
        double averagePricePerLitre,
        double totalFuelCost,
        Double averageConsumption,
        double averageRefuellingCost,
        Double cheapestPricePerLitre,
        Double mostExpensivePricePerLitre,
        List<FuelDataPointDTO> dataPoints
) {}