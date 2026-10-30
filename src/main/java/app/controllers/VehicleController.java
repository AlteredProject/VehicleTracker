package app.controllers;

import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;
public class VehicleController implements EndpointGroup {
    private final VehicleHandler vehicleHandler;

    public VehicleController(VehicleHandler vehicleHandler) {
        this.vehicleHandler = vehicleHandler;
    }

    @Override
    public void addEndpoints() {
        get("/api/v1/vehicles", vehicleHandler::getAllVehicles);
        post("/api/v1/vehicles", vehicleHandler::addVehicle);
        get("/api/v1/vehicles/{vehicleId}", vehicleHandler::getVehicle);
        post("/api/v1/vehicles/lookup", vehicleHandler::lookupVehicle);
        post("/api/v1/vehicles/manual", vehicleHandler::addManualVehicle);
        patch("/api/v1/vehicles/{vehicleId}", vehicleHandler::updateVehicle);
    }
}
