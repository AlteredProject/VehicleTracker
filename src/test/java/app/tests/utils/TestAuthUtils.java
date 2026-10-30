package app.tests.utils;

import app.entities.User;
import io.javalin.http.ContentType;
import io.restassured.RestAssured;
import jakarta.persistence.EntityManagerFactory;

public class TestAuthUtils {
    public static String loginAsTestUser() {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .body("""
                    {
                        "username": "John",
                        "password": "!johnbakke1"
                    }
                    """)
                .when()
                .post("/api/v1/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .path("token");
    }
}
