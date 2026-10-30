package app.integration;

import app.config.ApplicationConfig;
import app.config.HibernateTestConfig;
import app.entities.User;
import app.tests.utils.TestAuthUtils;
import app.tests.utils.TestDataFactory;
import app.testutils.TestDatabasePopulator;
import io.javalin.Javalin;
import io.restassured.RestAssured;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.util.Map;

import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MaintenanceEntryHandlerTest {
    private final EntityManagerFactory emf = HibernateTestConfig.getEntityManagerFactory();

    private Map<String, User> userMap;
    private String token;
    private Long vehicleId;
    private Long anotherUsersVehicleId;

    private Javalin app;

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

        anotherUsersVehicleId = TestDataFactory.createTestVehicle(emf, userMap.get("user2"));
    }

    @AfterAll
    void shutdown() {
        app.stop();
        emf.close();
    }

    @Test
    void createMaintenanceEntryReturns201() {
        String body = """
            {
                "date": "2026-10-29",
                "mileage": 25000,
                "category": "OIL_AND_FLUIDS",
                "description": "Engine oil and filter replaced",
                "cost": 899.95
            }
            """;

        RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .contentType(JSON)
                .body(body)
                .when()
                .post("/api/v1/vehicles/{vehicleId}/maintenance-entries",
                        vehicleId)
                .then()
                .statusCode(201)
                .body("entryId", notNullValue());
    }

    @Test
    void createMaintenanceEntryWithoutTokenReturns401() {
        RestAssured.given()
                .contentType(JSON)
                .body("""
                    {
                        "date": "2026-10-29",
                        "mileage": 25000,
                        "category": "OIL_AND_FLUIDS",
                        "description": "Oil change",
                        "cost": 899.95
                    }
                    """)
                .when()
                .post("/api/v1/vehicles/{vehicleId}/maintenance-entries",
                        vehicleId)
                .then()
                .statusCode(401);
    }

    @Test
    void createMaintenanceEntryWithInvalidDataReturns400() {
        RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .contentType(JSON)
                .body("""
                    {
                        "date": null,
                        "mileage": -100,
                        "category": "OIL_AND_FLUIDS",
                        "description": "",
                        "cost": -50
                    }
                    """)
                .when()
                .post("/api/v1/vehicles/{vehicleId}/maintenance-entries",
                        vehicleId)
                .then()
                .statusCode(400);
    }

    @Test
    void createMaintenanceEntryForAnotherUsersVehicleReturns404() {
        RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .contentType(JSON)
                .body("""
                    {
                        "date": "2026-10-29",
                        "mileage": 25000,
                        "category": "OIL_AND_FLUIDS",
                        "description": "Oil change",
                        "cost": 899.95
                    }
                    """)
                .when()
                .post("/api/v1/vehicles/{vehicleId}/maintenance-entries",
                        anotherUsersVehicleId)
                .then()
                .statusCode(404);
    }
}
