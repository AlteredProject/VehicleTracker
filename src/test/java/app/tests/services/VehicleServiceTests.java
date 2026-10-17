package app.tests.services;

import app.config.HibernateTestConfig;
import app.daos.VehicleDAO;
import app.dtos.MotorVehicleDTO;
import app.entities.Vehicle;
import app.services.VehicleService;
import app.testutils.TestDatabasePopulator;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class VehicleServiceTests {
    private final EntityManagerFactory emf = HibernateTestConfig.getEntityManagerFactory();

    private Map<String, Vehicle> vehicleMap;

    private VehicleDAO vehicleDAO;
    private VehicleService vehicleService;

    @BeforeEach
    void beforeEach() {
        vehicleMap = TestDatabasePopulator.createVehicles(emf);
        vehicleDAO = new VehicleDAO(emf);
        vehicleService = new VehicleService(vehicleDAO);
    }

    @AfterAll
    void shutdown() {
        emf.close();
    }

    // === VEHICLE CREATION ===
    @Test
    void createVehicle() {
        // Arrange
        MotorVehicleDTO vehicleDTO = new MotorVehicleDTO();
        vehicleDTO.setRegistrationNumber("AU60404");
        vehicleDTO.setMake("AUDI");
        vehicleDTO.setModel("A6");
        vehicleDTO.setModelYear(2015);

        // Act
        boolean result = vehicleService.createVehicle(vehicleDTO);

        // Assert
        assertTrue(result);
    }

    // === VEHICLE FETCHING ===
    @Test
    void getVehicle() {
        // Arrange
        Vehicle vehicle1 = vehicleMap.get("vehicle1");

        // Act
        Vehicle fetchedVehicle = vehicleService.getVehicle(1);

        // Assert
        assertAll(
            () -> assertEquals(vehicle1.getVehicleId(), fetchedVehicle.getVehicleId()),
            () -> assertEquals(vehicle1.getModel(), fetchedVehicle.getModel()),
            () -> assertEquals(vehicle1.getVin(), fetchedVehicle.getVin()),
            () -> assertEquals(vehicle1.getType(), fetchedVehicle.getType())
        );
    }

}
