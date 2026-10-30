package app.controllers.entry;

import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class MaintenanceEntryController implements EndpointGroup {
    private final MaintenanceEntryHandler maintenanceEntryHandler;

    public MaintenanceEntryController(MaintenanceEntryHandler maintenanceEntryHandler) {
        this.maintenanceEntryHandler = maintenanceEntryHandler;
    }

    @Override
    public void addEndpoints() {
        post("/api/v1/vehicles/{vehicleId}/maintenance-entries", maintenanceEntryHandler::addMaintenanceEntry);
        patch("/api/v1/vehicles/{vehicleId}/maintenance-entries/{maintenanceEntryId}", maintenanceEntryHandler::updateMaintenanceEntry);
    }
}
