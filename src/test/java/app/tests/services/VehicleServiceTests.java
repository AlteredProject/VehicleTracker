package app.tests.services;

import app.config.HibernateTestConfig;
import app.daos.MileageEntryDAO;
import app.daos.UserDAO;
import app.daos.VehicleDAO;
import app.dtos.vehicle.MotorVehicleDTO;
import app.entities.User;
import app.entities.Vehicle;
import app.dtos.vehicle.VehicleCreationResultDTO;
import app.services.MotorAPIService;
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
    private final MotorAPIService motorAPIService = new MotorAPIService();
    private final MileageEntryDAO mileageEntryDAO = new MileageEntryDAO(emf);

    private Map<String, User> userMap;
    private Map<String, Vehicle> vehicleMap;

    private UserDAO userDAO;
    private VehicleDAO vehicleDAO;
    private VehicleService vehicleService;

    @BeforeEach
    void beforeEach() {
        userMap = TestDatabasePopulator.createUsers(emf);
        vehicleMap = TestDatabasePopulator.createVehicles(emf, userMap);

        vehicleDAO = new VehicleDAO(emf);
        userDAO = new UserDAO(emf);
        vehicleService = new VehicleService(emf, vehicleDAO, userDAO, mileageEntryDAO, motorAPIService);
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
        VehicleCreationResultDTO result = vehicleService.createVehicle(vehicleDTO, 1);

        // Assert
        assertTrue(result.success());
        assertEquals("Vehicle Successfully created", result.message());
    }

    // === VEHICLE OWNERSHIP ===
    @Test
    void createVehicleBelongsToCorrectUser() {
        // Arrange
        MotorVehicleDTO vehicleDTO = new MotorVehicleDTO();
        vehicleDTO.setRegistrationNumber("AU60404");
        vehicleDTO.setMake("AUDI");
        vehicleDTO.setModel("A6");
        vehicleDTO.setModelYear(2015);

        User user = userMap.get("user1");

        // Act
        VehicleCreationResultDTO result = vehicleService.createVehicle(vehicleDTO, user.getUserId());

        // Assert
        assertTrue(result.success());
        assertEquals("Vehicle Successfully created", result.message());

        Vehicle savedVehicle = vehicleDAO.getVehicleByRegistrationNumber("AU60404");

        assertNotNull(savedVehicle);
        assertEquals(user.getUserId(), savedVehicle.getUser().getUserId());
    }

    // === VEHICLE FETCHING ===
    @Test
    void getVehicle() {
        // Arrange
        Vehicle vehicle1 = vehicleMap.get("vehicle1");
        User user1 = userMap.get("user1");

        // Act
        Vehicle fetchedVehicle = vehicleService.getVehicle(1L, user1.getUserId());

        // Assert
        assertAll(
            () -> assertEquals(vehicle1.getVehicleId(), fetchedVehicle.getVehicleId()),
            () -> assertEquals(vehicle1.getModel(), fetchedVehicle.getModel()),
            () -> assertEquals(vehicle1.getVin(), fetchedVehicle.getVin()),
            () -> assertEquals(vehicle1.getType(), fetchedVehicle.getType())
        );
    }

}
