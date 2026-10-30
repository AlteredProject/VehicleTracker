package app.controllers.entry;

import app.dtos.entry.MileageEntryRequestDTO;
import app.entities.MileageEntry;
import app.services.MileageEntryService;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

import java.util.Map;

public class MileageEntryHandler {
    private final MileageEntryService mileageEntryService;

    public MileageEntryHandler(MileageEntryService mileageEntryService) {
        this.mileageEntryService = mileageEntryService;
    }

    public void addMileageEntry(Context ctx) {
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

        MileageEntryRequestDTO dto = ctx.bodyAsClass(MileageEntryRequestDTO.class);

        MileageEntry entry = mileageEntryService.createMileageEntry(dto, vehicleId, userId);

        ctx.status(HttpStatus.CREATED).json(Map.of("message", "Mileage entry created", "entryId", entry.getEntryId()));
    }
}
