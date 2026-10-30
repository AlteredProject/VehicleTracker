package app.dtos.vehicle;

public record VehicleUpdateDTO(String type, String make, String model, String variant, String chassisType, Integer modelYear, String color, String fuelType, Double engineVolume, Double enginePower, Float mileage) {}