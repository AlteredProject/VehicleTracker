package app.controllers.entry;

import app.dtos.entry.FuelEntryRequestDTO;
import app.dtos.entry.FuelEntryUpdateDTO;
import app.entities.FuelEntry;
import app.services.FuelEntryService;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

import java.util.Map;

public class FuelEntryHandler {
    private final FuelEntryService fuelEntryService;

    public FuelEntryHandler(FuelEntryService fuelEntryService) {
        this.fuelEntryService = fuelEntryService;
    }

    public void addFuelEntry(Context ctx) {
        Integer userId = ctx.attribute("userId");

        if(userId == null) {
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

        FuelEntryRequestDTO dto = ctx.bodyAsClass(FuelEntryRequestDTO.class);

        FuelEntry entry = fuelEntryService.createFuelEntry(dto, vehicleId, userId);

        ctx.status(HttpStatus.CREATED).json(Map.of("message", "Fuel entry created", "entryId", entry.getEntryId()));
    }

    public void updateFuelEntry(Context ctx) {
        Integer userId = ctx.attribute("userId");
        Long vehicleId = Long.parseLong(ctx.pathParam("vehicleId"));
        Long fuelEntryId = Long.parseLong(ctx.pathParam("fuelEntryId"));

        if(userId == null) {
            ctx.status(HttpStatus.UNAUTHORIZED).json(Map.of("message", "Unauthorized"));
            return;
        }

        FuelEntryUpdateDTO dto = ctx.bodyAsClass(FuelEntryUpdateDTO.class);

        fuelEntryService.updateFuelEntry(dto, vehicleId, fuelEntryId, userId);

        ctx.status(HttpStatus.OK).json(Map.of("message", "Fuel entry updated successfully"));
    }
}
