package app.integration;

import app.config.ApplicationConfig;
import app.config.HibernateTestConfig;
import app.entities.User;
import app.entities.Vehicle;
import app.tests.utils.TestAuthUtils;
import app.tests.utils.TestDataFactory;
import app.testutils.TestDatabasePopulator;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.javalin.Javalin;
import org.junit.jupiter.api.*;

import jakarta.persistence.EntityManagerFactory;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class StatisticsIntegrationTest {

    private final EntityManagerFactory emf = HibernateTestConfig.getEntityManagerFactory();

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
        Map<String, User> userMap = TestDatabasePopulator.createUsers(emf);
        Map<String, Vehicle> vehicleMap = TestDatabasePopulator.createVehicles(emf, userMap);

        TestDatabasePopulator.createEntries(emf, vehicleMap);

        token = TestAuthUtils.loginAsTestUser();
        vehicleId = TestDataFactory.createTestVehicle(emf, userMap.get("user1"));
    }

    @AfterAll
    void shutdown() {
        app.stop();
        emf.close();
    }

    @Test
    void getStatistics_week_returnsCorrectStatistics() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/v1/vehicles/" + vehicleId + "/statistics?period=WEEK")
                .then()
                .statusCode(200)
                .body("period", equalTo("WEEK"))
                .body("expenses", notNullValue())
                .body("fuel", notNullValue())
                .body("mileage", notNullValue());
    }

    @Test
    void getStatistics_month_returnsCorrectStatistics() {
        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/v1/vehicles/" + vehicleId + "/statistics?period=MONTH")
                .then()
                .statusCode(200)
                .body("period", equalTo("MONTH"));
    }

    @Test
    void getStatistics_year_returnsCorrectStatistics() {
        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/v1/vehicles/" + vehicleId + "/statistics?period=YEAR")
                .then()
                .statusCode(200)
                .body("period", equalTo("YEAR"));
    }

    @Test
    void getStatistics_all_returnsLifetimeStatistics() {
        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/v1/vehicles/" + vehicleId + "/statistics?period=ALL")
                .then()
                .statusCode(200)
                .body("period", equalTo("ALL"))
                .body("startDate", nullValue());
    }

    @Test
    void getStatistics_invalidPeriod_returnsBadRequest() {
        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/v1/vehicles/" + vehicleId + "/statistics?period=INVALID")
                .then()
                .statusCode(400);
    }

    @Test
    void getStatistics_withoutToken_returnsUnauthorized() {
        given()
                .when()
                .get("/api/v1/vehicles/" + vehicleId + "/statistics?period=MONTH")
                .then()
                .statusCode(401);
    }

    @Test
    void getStatistics_vehicleDoesNotExist_returnsNotFound() {
        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/v1/vehicles/999999/statistics?period=MONTH")
                .then()
                .statusCode(404);
    }
}