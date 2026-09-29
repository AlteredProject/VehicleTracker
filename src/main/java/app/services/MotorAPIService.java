package app.services;

import app.dtos.MotorVehicleDTO;

public class MotorAPIService {
    private static final String BASE_URL = "https://v1.motorapi.dk/vehicles/";
    private final APIReader apiReader;
    private final String apiKey = System.getenv("API_KEY");

    public MotorAPIService() {
        this.apiReader = new APIReader();
    }

    public MotorVehicleDTO getVehicle(String registrationNumber) {
        String url = BASE_URL + registrationNumber.toUpperCase();

        return apiReader.get(url, MotorVehicleDTO.class, "X-AUTH-TOKEN", apiKey);
    }
}