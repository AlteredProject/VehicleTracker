package app;

import app.dtos.MotorVehicleDTO;
import app.services.MotorAPIService;

public class Main {
    static void main() {
        String apiKey = System.getenv("API_KEY");

        String registrationNumber = "CL65485";

        MotorAPIService motorAPIService = new MotorAPIService(apiKey);

        MotorVehicleDTO vehicle = motorAPIService.getVehicle(registrationNumber);

        System.out.println(vehicle);
    }
}
