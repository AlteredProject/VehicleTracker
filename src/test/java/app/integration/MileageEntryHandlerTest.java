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
public class MileageEntryHandlerTest {
    private final EntityManagerFactory emf = HibernateTestConfig.getEntityManagerFactory();

    private Map<String, User> userMap;
    private String token;
    private Long vehicleId;

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
    }

    @AfterAll
    void shutdown() {
        app.stop();
        emf.close();
    }

    @Test
    void createManualMileageEntryReturns201() {
        String body = """
        {
            "date": "2026-10-29",
            "mileage": 25200
        }
        """;

        RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .contentType(JSON)
                .body(body)
                .when()
                .post("/api/v1/vehicles/{vehicleId}/mileage-entries",
                        vehicleId)
                .then()
                .statusCode(201)
                .body("entryId", notNullValue());
    }
}
