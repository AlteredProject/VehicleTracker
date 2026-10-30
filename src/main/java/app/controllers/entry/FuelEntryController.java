package app.controllers.entry;

import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class FuelEntryController implements EndpointGroup {
    private final FuelEntryHandler fuelEntryHandler;

    public FuelEntryController(FuelEntryHandler fuelEntryHandler) {
        this.fuelEntryHandler = fuelEntryHandler;
    }

    @Override
    public void addEndpoints() {
        post("/api/v1/vehicles/{vehicleId}/fuel-entries", fuelEntryHandler::addFuelEntry);
        patch("/api/v1/vehicles/{vehicleId}/fuel-entries/{fuelEntryId}", fuelEntryHandler::updateFuelEntry);
    }
}
