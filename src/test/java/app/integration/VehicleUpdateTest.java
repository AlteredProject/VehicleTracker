package app.integration;

import app.config.ApplicationConfig;
import app.config.HibernateTestConfig;
import app.entities.MileageEntry;
import app.entities.User;
import app.entities.Vehicle;
import app.enums.MileageEntrySource;
import app.tests.utils.TestAuthUtils;
import app.tests.utils.TestDataFactory;
import app.testutils.TestDatabasePopulator;
import io.javalin.Javalin;
import io.javalin.http.ContentType;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class VehicleUpdateTest {

    private final EntityManagerFactory emf = HibernateTestConfig.getEntityManagerFactory();

    private Map<String, User> userMap;
    private Javalin app;
    private String token;
    private Long vehicleId;

    @BeforeAll
    void beforeAll() {
        app = new ApplicationConfig(emf).start(7070);

        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 7070;
    }

    @BeforeEach
    void beforeEach() {
        userMap = TestDatabasePopulator.createUsers(emf);
        token = TestAuthUtils.loginAsTestUser();

        vehicleId = TestDataFactory.createTestVehicle(emf, userMap.get("user1"));
    }

    @AfterAll
    void afterAll() {
        app.stop();
        emf.close();
    }

    private Response updateVehicle(Map<String, Object> body, Long id) {
        Response response = given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .patch("/api/v1/vehicles/" + id);

        response.then().log().all();

        return response;
    }

    private Vehicle getVehicle(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.find(Vehicle.class, id);
        }
    }

    private long getMileageEntryCount(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery("SELECT COUNT(m) FROM MileageEntry m WHERE m.vehicle.vehicleId = :id", Long.class)
                    .setParameter("id", id)
                    .getSingleResult();
        }
    }

    @Test
    void updateVehicleInformation() {
        long initialEntryCount = getMileageEntryCount(vehicleId);

        Map<String, Object> body = Map.of("color", "Black", "enginePower", 200.0);

        Response response = updateVehicle(body, vehicleId);

        assertEquals(200, response.statusCode());

        Vehicle updated = getVehicle(vehicleId);

        assertEquals("Black", updated.getColor());
        assertEquals(200.0, updated.getEnginePower());

        assertEquals(initialEntryCount, getMileageEntryCount(vehicleId));
    }

    @Test
    void updateMileageCreatesMileageEntry() {
        long initialEntryCount = getMileageEntryCount(vehicleId);

        Map<String, Object> body = Map.of("mileage", 45000.0);

        Response response = updateVehicle(body, vehicleId);

        assertEquals(200, response.statusCode());

        Vehicle updated = getVehicle(vehicleId);

        assertEquals(45000.0f, updated.getMileage(), 0.01f);
        assertEquals(initialEntryCount + 1, getMileageEntryCount(vehicleId));

        try (EntityManager em = emf.createEntityManager()) {
            List<MileageEntry> entries = em.createQuery("SELECT m FROM MileageEntry m WHERE m.vehicle.vehicleId = :id AND m.source = :source", MileageEntry.class)
                    .setParameter("id", vehicleId)
                    .setParameter("source", MileageEntrySource.MANUAL)
                    .getResultList();

            assertFalse(entries.isEmpty());
            assertTrue(entries.stream().anyMatch(entry -> Float.compare(entry.getMileage(), 45000.0f) == 0));
        }
    }

    @Test
    void updateVehicleInformationAndMileage() {
        long initialEntryCount = getMileageEntryCount(vehicleId);

        Map<String, Object> body = Map.of("color", "Black", "modelYear", 2015, "mileage", 45000.0);

        Response response = updateVehicle(body, vehicleId);

        assertEquals(200, response.statusCode());

        Vehicle updated = getVehicle(vehicleId);

        assertEquals("Black", updated.getColor());
        assertEquals(2015, updated.getModelYear());
        assertEquals(45000.0f, updated.getMileage(), 0.01f);

        assertEquals(initialEntryCount + 1, getMileageEntryCount(vehicleId));
    }

    @Test
    void cannotUpdateAnotherUsersVehicle() {
        Long otherVehicleId = TestDataFactory.createTestVehicle(emf, userMap.get("user2"));

        Map<String, Object> body = Map.of("color", "Black");

        Response response = updateVehicle(body, otherVehicleId);

        assertEquals(404, response.statusCode());

        Vehicle otherVehicle = getVehicle(otherVehicleId);

        assertNotEquals("Black", otherVehicle.getColor());
    }

    @Test
    void updateVehicleWithNegativeMileage() {
        long initialEntryCount = getMileageEntryCount(vehicleId);

        Map<String, Object> body = Map.of("mileage", -100.0);

        Response response = updateVehicle(body, vehicleId);

        assertEquals(400, response.statusCode());

        Vehicle vehicle = getVehicle(vehicleId);

        assertNotEquals(-100.0f, vehicle.getMileage());

        assertEquals(initialEntryCount, getMileageEntryCount(vehicleId));
    }
}
