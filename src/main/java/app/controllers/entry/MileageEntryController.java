package app.controllers.entry;

import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.post;

public class MileageEntryController implements EndpointGroup {
    private final MileageEntryHandler mileageEntryHandler;

    public MileageEntryController(MileageEntryHandler mileageEntryHandler) {
        this.mileageEntryHandler = mileageEntryHandler;
    }

    @Override
    public void addEndpoints() {
        post("/api/v1/vehicles/{vehicleId}/mileage-entries", mileageEntryHandler::addMileageEntry);
    }
}
