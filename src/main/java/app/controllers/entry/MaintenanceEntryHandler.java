package app.controllers.entry;

import app.dtos.entry.MaintenanceEntryRequestDTO;
import app.dtos.entry.MaintenanceEntryUpdateDTO;
import app.entities.MaintenanceEntry;
import app.services.MaintenanceEntryService;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

import java.util.Map;

public class MaintenanceEntryHandler {
    private final MaintenanceEntryService maintenanceEntryService;

    public MaintenanceEntryHandler(MaintenanceEntryService maintenanceEntryService) {
        this.maintenanceEntryService = maintenanceEntryService;
    }

    public void addMaintenanceEntry(Context ctx) {
        Integer userId = ctx.attribute("userId");

        if (userId == null) {
            ctx.status(HttpStatus.UNAUTHORIZED).json(Map.of("message", "Unauthorized"));
            return;
        }

        Long vehicleId;

        try {
            vehicleId = Long.valueOf(ctx.pathParam("vehicleId"));
        } catch (NumberFormatException e) {
            ctx.status(HttpStatus.BAD_REQUEST).json(Map.of("message", "Invalid vehicle ID"));
            return;
        }

        MaintenanceEntryRequestDTO dto = ctx.bodyAsClass(MaintenanceEntryRequestDTO.class);

        MaintenanceEntry entry = maintenanceEntryService.createMaintenanceEntry(dto, vehicleId, userId);

        ctx.status(HttpStatus.CREATED).json(Map.of("message", "Maintenance entry created", "entryId", entry.getEntryId()));
    }

    public void updateMaintenanceEntry(Context ctx) {
        Integer userId = ctx.attribute("userId");
        Long vehicleId = Long.valueOf(ctx.pathParam("vehicleId"));
        Long maintenanceEntryId = Long.valueOf(ctx.pathParam("maintenanceEntryId"));

        if (userId == null) {
            ctx.status(HttpStatus.UNAUTHORIZED).json(Map.of("message", "Unauthorized"));
            return;
        }

        MaintenanceEntryUpdateDTO dto = ctx.bodyAsClass(MaintenanceEntryUpdateDTO.class);

        maintenanceEntryService.updateMaintenanceEntry(dto, vehicleId, maintenanceEntryId, userId);

        ctx.status(200).json(Map.of("message", "Maintenance entry updated successfully"));
    }
}
