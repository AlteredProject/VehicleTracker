package app.controllers;

import app.dtos.vehicle.*;
import app.entities.Vehicle;
import app.services.VehicleService;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

import java.util.List;
import java.util.Map;

public class VehicleHandler {
    private final VehicleService vehicleService;

    public VehicleHandler(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    public void getAllVehicles(Context ctx)  {
        Integer userId = ctx.attribute("userId");

        if(userId == null) {
            ctx.status(HttpStatus.UNAUTHORIZED).json(Map.of("message", "Unauthorized"));
            return;
        }

        List<Vehicle> vehiclesList = vehicleService.getAllVehiclesFromUser(userId);

        ctx.status(HttpStatus.OK).json(vehiclesList);
    }

    public void addVehicle(Context ctx) {
        MotorVehicleDTO dto = ctx.bodyAsClass(MotorVehicleDTO.class);
        Integer userId = ctx.attribute("userId");

        if (userId == null) {
            ctx.status(HttpStatus.UNAUTHORIZED).json(Map.of("message", "Unauthorized"));
            return;
        }

        VehicleCreationResultDTO result = vehicleService.createVehicle(dto, userId);

        if(!result.success()) {
            ctx.status(HttpStatus.BAD_REQUEST).json(Map.of("message", result.message()));
            return;
        }

        ctx.status(HttpStatus.CREATED).json(Map.of("message", result.message()));
    }

    public void getVehicle(Context ctx) {
        Integer userId = ctx.attribute("userId");
        Long vehicleId = Long.valueOf(ctx.pathParam("vehicleId"));

        if(userId == null) {
            ctx.status(HttpStatus.UNAUTHORIZED).json(Map.of("message", "Unauthorized"));
            return;
        }

        Vehicle vehicle = vehicleService.getVehicle(vehicleId, userId);

        if (vehicle == null) {
            ctx.status(HttpStatus.NOT_FOUND).json(Map.of("message", "Vehicle not found"));
            return;
        }

        ctx.json(vehicle);
    }

    public void lookupVehicle(Context ctx) {
        Integer userId = ctx.attribute("userId");

        if(userId == null) {
            ctx.status(HttpStatus.UNAUTHORIZED).json(Map.of("message", "Unauthorized"));
            return;
        }

        VehicleLookupRequestDTO request = ctx.bodyAsClass(VehicleLookupRequestDTO.class);

        MotorVehicleDTO vehicle = vehicleService.lookupVehicle(request.registrationNumber());

        if(vehicle == null ) {
            ctx.status(HttpStatus.OK).json(Map.of("found", false, "message", "Vehicle not found. You can add it manually"));
            return;
        }

        ctx.status(HttpStatus.OK).json(Map.of("found", true, "vehicle", vehicle));
    }

    public void addManualVehicle(Context ctx) {
        Integer userId = ctx.attribute("userId");

        if(userId == null) {
            ctx.status(HttpStatus.UNAUTHORIZED).json(Map.of("message", "Unauthorized"));
            return;
        }

        ManualVehicleCreationDTO dto = ctx.bodyAsClass(ManualVehicleCreationDTO.class);

        VehicleCreationResultDTO result = vehicleService.createManualVehicle(dto, userId);

        ctx.status(HttpStatus.CREATED).json(Map.of("message", result.message()));
    }

    public void updateVehicle(Context ctx) {
        Integer userId = ctx.attribute("userId");
        Long vehicleId = Long.parseLong(ctx.pathParam("vehicleId"));

        if(userId == null) {
            ctx.status(HttpStatus.UNAUTHORIZED).json(Map.of("message", "Unauthorized"));
            return;
        }

        VehicleUpdateDTO dto = ctx.bodyAsClass(VehicleUpdateDTO.class);

        Vehicle vehicle = vehicleService.updateVehicle(dto, vehicleId, userId);

        ctx.status(HttpStatus.OK).json(Map.of("message", "Vehicle updated successfully"));
    }
}
