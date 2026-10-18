package app.tests.utils;

import app.config.HibernateTestConfig;
import app.utils.PasswordEncrypter;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PasswordEncrypterTest {
    private final EntityManagerFactory emf = HibernateTestConfig.getEntityManagerFactory();

    PasswordEncrypter passwordEncrypter = new PasswordEncrypter();

    @BeforeEach
    void beforeEach() {
    }

    @AfterAll
    void shutdown() {
        emf.close();
    }

    @Test
    void hashPassword() {
        // Arrange
        String password = "unHashedPassword";

        // Act
        String hashedPassword = passwordEncrypter.hashPassword(password);

        // Assert
        assertNotEquals(password, hashedPassword);
    }

    @Test
    void checkPassword() {
        // Arrange
        String password = "unHashedPassword";
        String hashedPassword = passwordEncrypter.hashPassword(password);

        // Act
        boolean result = passwordEncrypter.checkPassword(password, hashedPassword);

        // Assert
        assertTrue(result);
    }

}
