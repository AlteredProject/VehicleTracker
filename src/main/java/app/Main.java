package app;

import app.config.HibernateConfig;
import app.dtos.MotorVehicleDTO;
import app.services.MotorAPIService;
import app.services.VehicleTrackerService;
import jakarta.persistence.EntityManagerFactory;

public class Main {
    static void main() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        MotorAPIService motorAPIService = new MotorAPIService();
        VehicleTrackerService vehicleTrackerService = new VehicleTrackerService(emf);

        String registrationNumber = "CL65485";


        MotorVehicleDTO vehicle = motorAPIService.getVehicle(registrationNumber);

        vehicleTrackerService.createVehicle(vehicle);

        vehicleTrackerService.getVehicle(1);
    }
}
