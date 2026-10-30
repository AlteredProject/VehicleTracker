package app.dtos.statistics;

import app.enums.StatisticsPeriod;

import java.time.LocalDate;

public record VehicleStatisticsDTO(
        StatisticsPeriod period,
        LocalDate startDate,
        LocalDate endDate,
        ExpenseStatisticsDTO expenses,
        FuelStatisticsDTO fuel,
        MileageStatisticsDTO mileage
) {}
