package app;

import app.config.HibernateConfig;
import app.daos.*;
import app.dtos.MotorVehicleDTO;
import app.dtos.UserRegistryDTO;
import app.services.MotorAPIService;
import app.services.UserService;
import app.services.VehicleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.javalin.Javalin;
import io.javalin.json.JavalinJackson;
import jakarta.persistence.EntityManagerFactory;

public class Main {
    static void main() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();

        MotorAPIService motorAPIService = new MotorAPIService();


        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        FuelDAO fuelDAO = new FuelDAO(emf);
        MaintenanceDAO maintenanceDAO = new MaintenanceDAO(emf);
        MileageDAO mileageDAO = new MileageDAO(emf);
        VehicleDAO vehicleDAO = new VehicleDAO(emf);
        UserDAO userDAO = new UserDAO(emf);

        VehicleService vehicleService = new VehicleService(vehicleDAO);
        UserService userService = new UserService(userDAO);

        Javalin app = Javalin.create(config -> {
            config.showJavalinBanner = false;
            config.jsonMapper(new JavalinJackson().updateMapper(mapper -> mapper.registerModule(new JavaTimeModule())));
        });
        app.start(7070);

        String registrationNumber = "CL65485";

        UserRegistryDTO userRegistration = new UserRegistryDTO(
        "John Bakke",
        "john.bakke@gmail.com",
        "21282224",
        "!johnbakke1",
        "!johnbakke1"
        );

        userService.createUser(userRegistration);
        userService.getUser(1);

        /* === Finds and creates vehicle by registrationNumber ===
        MotorVehicleDTO vehicle = motorAPIService.getVehicle(registrationNumber);
        vehicleService.createVehicle(vehicle);
        vehicleService.getVehicle(1);
         */
    }
}
