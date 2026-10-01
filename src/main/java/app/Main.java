package app;

import app.config.HibernateConfig;
import app.dtos.MotorVehicleDTO;
import app.services.MotorAPIService;
import app.services.VehicleTrackerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.javalin.Javalin;
import io.javalin.json.JavalinJackson;
import jakarta.persistence.EntityManagerFactory;

public class Main {
    static void main() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();

        MotorAPIService motorAPIService = new MotorAPIService();

        VehicleTrackerService vehicleTrackerService = new VehicleTrackerService(emf);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        Javalin app = Javalin.create(config -> {
            config.showJavalinBanner = false;
            config.jsonMapper(new JavalinJackson().updateMapper(mapper -> mapper.registerModule(new JavaTimeModule())));
        });
        app.start(7070);

        String registrationNumber = "CL65485";


        MotorVehicleDTO vehicle = motorAPIService.getVehicle(registrationNumber);

        vehicleTrackerService.createVehicle(vehicle);

        vehicleTrackerService.getVehicle(1);
    }
}
