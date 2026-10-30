package app.controllers;

import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class StatisticsController implements EndpointGroup {
    private final StatisticsHandler statisticsHandler;

    public StatisticsController(StatisticsHandler statisticsHandler) {
        this.statisticsHandler = statisticsHandler;
    }

    @Override
    public void addEndpoints() {
        path("/api/v1/vehicles/{vehicleId}/statistics", () -> get(statisticsHandler::getStatistics));
    }
}
