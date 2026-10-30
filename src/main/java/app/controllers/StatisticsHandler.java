package app.controllers;

import app.dtos.statistics.VehicleStatisticsDTO;
import app.enums.StatisticsPeriod;
import app.exceptions.BadRequestException;
import app.services.StatisticsService;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

import java.util.Map;

public class StatisticsHandler {
    private final StatisticsService statisticsService;

    public StatisticsHandler(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    public void getStatistics(Context ctx) {
        Integer userId = ctx.attribute("userId");
        Long vehicleId = Long.valueOf(ctx.pathParam("vehicleId"));
        String periodParam = ctx.queryParam("period");

        if(userId == null) {
            ctx.status(HttpStatus.UNAUTHORIZED).json(Map.of("message", "Unauthorized"));
            return;
        }

        if (periodParam == null || periodParam.isBlank()) {
            throw new BadRequestException("Statistics period is required");
        }

        StatisticsPeriod period;

        try {
            period = StatisticsPeriod.valueOf(periodParam.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid statistics period");
        }

        VehicleStatisticsDTO statistics = statisticsService.getStatistics(vehicleId, userId, period);

        ctx.status(HttpStatus.OK).json(statistics);
    }
}
