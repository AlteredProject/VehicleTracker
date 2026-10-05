package app.services;

import app.daos.VehicleDAO;
import app.dtos.MotorVehicleDTO;
import app.entities.Vehicle;

import java.time.LocalDate;

public class VehicleService {
    private final VehicleDAO vehicleDAO;

    public VehicleService(VehicleDAO vehicleDAO) {
        this.vehicleDAO = vehicleDAO;
    }

    public void createVehicle(MotorVehicleDTO vehicleDTO) {
        Vehicle existingVehicle = vehicleDAO.getVehicleByRegistrationNumber(vehicleDTO.getRegistrationNumber());

        if(existingVehicle != null) {
            System.out.println("Vehicle already exists. Skipping...");
            return;
        }

        Vehicle vehicle = new Vehicle();

        vehicle.setRegistrationNumber(vehicleDTO.getRegistrationNumber());
        vehicle.setStatus(vehicleDTO.getStatus());
        vehicle.setVin(vehicleDTO.getVin());
        vehicle.setType(vehicleDTO.getType());
        vehicle.setMake(vehicleDTO.getMake());
        vehicle.setModel(vehicleDTO.getModel());
        vehicle.setVariant(vehicleDTO.getVariant());
        vehicle.setChassisType(vehicleDTO.getChassisType());
        vehicle.setModelYear(vehicleDTO.getModelYear());
        vehicle.setColor(vehicleDTO.getColor());
        vehicle.setFuelType(vehicleDTO.getType());
        vehicle.setEngineVolume(vehicleDTO.getEngineVolume());
        vehicle.setEnginePower(vehicleDTO.getEnginePower());

        vehicle.setCreatedAt(LocalDate.now());
        vehicle.setUpdatedAt(LocalDate.now());

        vehicleDAO.create(vehicle);
        System.out.println("Vehicle Successfully created");
    }

    public void getVehicle(int id) {
        Vehicle vehicle = vehicleDAO.getById(id);
        System.out.println(vehicle);
    }

}
