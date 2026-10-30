package app.dtos.statistics;

import java.util.List;

public record MileageStatisticsDTO(
        double currentMileage,
        Double totalDistance,
        Double averageDailyDistance,
        Double averageMonthlyDistance,
        List<MileageDataPointDTO> dataPoints
) {}
