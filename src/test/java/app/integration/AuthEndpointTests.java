package app.integration;

import app.config.ApplicationConfig;
import app.config.HibernateTestConfig;
import app.entities.User;
import app.testutils.TestDatabasePopulator;
import io.javalin.Javalin;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthEndpointTests {
    private final EntityManagerFactory emf = HibernateTestConfig.getEntityManagerFactory();

    private Map<String, User> userMap;

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
    }

    @AfterAll
    void shutdown() {
        app.stop();
        emf.close();
    }

    @Test
    void registerUser() {
        given()
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "username", "TestUser",
                        "email", "testuser@example.com",
                        "phone", "12345678",
                        "password", "TestPassword123!",
                        "passwordRepeat", "TestPassword123!"
                ))
        .when()
                .post("/api/users")
        .then()
                .statusCode(201)
                .body("message", equalTo("User created successfully"));
    }

    @Test
    void loginSuccessfully() {
        given()
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "username", "John",
                        "password", "!johnbakke1"
                ))
        .when()
                .post("/api/auth/login")
        .then()
                .statusCode(200)
                .body("message", equalTo("Successfully Logged In"))
                .body("token", not(blankOrNullString()));
    }

    @Test
    void loginWithIncorrectPassword() {
        given()
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "username", "John",
                        "password", "WrongPassword123!"
                ))
        .when()
                .post("/api/auth/login")
        .then()
                .statusCode(401)
                .body("message", equalTo("Invalid username or password."));
    }

    @Test
    void loginWithUnknownUsername() {
        given()
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "username", "UnknownUser",
                        "password", "SomePassword123!"
                ))
        .when()
                .post("/api/auth/login")
        .then()
                .statusCode(401)
                .body("message", equalTo("Invalid username or password."));
    }

    @Test
    void accessProtectedEndpointWithValidToken() {
        String token =
                given()
                        .contentType(ContentType.JSON)
                        .body(Map.of(
                                "username", "John",
                                "password", "!johnbakke1"
                        ))
                .when()
                        .post("/api/auth/login")
                .then()
                        .statusCode(200)
                        .extract()
                        .path("token");

        Integer expectedUserId = userMap.get("user1").getUserId();

        given()
                .header("Authorization", "Bearer " + token)
        .when()
                .get("/api/test/protected")
        .then()
                .statusCode(200)
                .body("userId", equalTo(expectedUserId));
    }

    @Test
    void protectedEndpointRejectsMissingToken() {
        given()
        .when()
                .get("/api/test/protected")
        .then()
                .statusCode(401);
    }

    @Test
    void protectedEndpointRejectsInvalidToken() {
        given()
                .header("Authorization", "Bearer invalid-token")
        .when()
                .get("/api/test/protected")
        .then()
                .statusCode(401);
    }
}
