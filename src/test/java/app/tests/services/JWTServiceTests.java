package app.tests.services;

import app.config.HibernateTestConfig;
import app.services.JWTService;
import app.testutils.TestJWTConfig;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class JWTServiceTests {
    private final EntityManagerFactory emf = HibernateTestConfig.getEntityManagerFactory();

    private JWTService jwtService;

    @BeforeEach
    void beforeEach() {
        jwtService = new JWTService(TestJWTConfig.JWT_SECRET);
    }

    @AfterAll
    void shutdown() {
        emf.close();
    }

    @Test
    void generateToken() {
        // Arrange

        // Act
        String token = jwtService.generateToken(1);

        // Assert
        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void getUserIdFromToken() {
        // Arrange

        // Act
        String token = jwtService.generateToken(42);
        Integer userId = jwtService.getUserIdFromToken(token);

        // Assert
        assertEquals(42, userId);
    }

    @Test
    void isTokenValid() {
        // Arrange

        // Act
        String token = jwtService.generateToken(42);

        // Assert
        assertTrue(jwtService.isTokenValid(token));
    }

    @Test
    void isTokenInvalid() {
        // Arrange
        // Act

        // Assert
        assertFalse(jwtService.isTokenValid("invalid-token"));
    }
}
