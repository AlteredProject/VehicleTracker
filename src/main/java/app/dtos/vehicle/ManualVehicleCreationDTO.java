package app.dtos.vehicle;

public record ManualVehicleCreationDTO(String registrationNumber, String type, String make, String model, Integer modelYear, String color, String fuelType, String vin, String variant, String chassisType, Double engineVolume, Double enginePower, Float mileage) {}
