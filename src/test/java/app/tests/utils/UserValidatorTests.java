package app.tests.utils;

import app.config.HibernateTestConfig;
import app.dtos.UserRegistryDTO;
import app.entities.User;
import app.testutils.TestDatabasePopulator;
import app.utils.UserValidator;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserValidatorTests {
    private final EntityManagerFactory emf = HibernateTestConfig.getEntityManagerFactory();

    private Map<String, User> userMap;
    
    private UserValidator userValidator;

    @BeforeEach
    void beforeEach() {
        userMap = TestDatabasePopulator.createUsers(emf);
        userValidator = new UserValidator();
    }

    @AfterAll
    void shutdown() {
        emf.close();
    }
    
    @Test
    void validate_NoErrors() {
        // Arrange
        UserRegistryDTO userRegistration = new UserRegistryDTO(
            "John Bakke",
            "john.bakke@gmail.com",
            "21282224",
            "!johnbakke1",
            "!johnbakke1"
        );
        
        // Act
        List<String> validationErrors = userValidator.validate(userRegistration);
        
        // Assert
        assertTrue(validationErrors.isEmpty());
    }

    @Test
    void validate_NameError() {
        // Arrange
        UserRegistryDTO userRegistration = new UserRegistryDTO(
            "This name is way too long to be accepted",
            "john.bakke@gmail.com",
            "21282224",
            "!johnbakke1",
            "!johnbakke1"
        );

        // Act
        List<String> validationErrors = userValidator.validate(userRegistration);

        // Assert
        assertTrue(validationErrors.contains("Name must be between 6 and 20 characters"));
        assertEquals(1, validationErrors.size());
    }

    @Test
    void validate_EmailError() {
        // Arrange
        UserRegistryDTO userRegistration = new UserRegistryDTO(
            "John Bakke",
            "johnBakkeMail",
            "21282224",
            "!johnbakke1",
            "!johnbakke1"
        );

        // Act
        List<String> validationErrors = userValidator.validate(userRegistration);

        // Assert
        assertTrue(validationErrors.contains("Not a valid email"));
        assertEquals(1, validationErrors.size());
    }

    @Test
    void validate_PhoneNumberError() {
        // Arrange
        UserRegistryDTO userRegistration = new UserRegistryDTO(
            "John Bakke",
            "john.bakke@gmail.com",
            "112",
            "!johnbakke1",
            "!johnbakke1"
        );

        // Act
        List<String> validationErrors = userValidator.validate(userRegistration);

        // Assert
        assertTrue(validationErrors.contains("Not a valid phone number"));
        assertEquals(1, validationErrors.size());
    }

    @Test
    void validate_PasswordError_NoNumber() {
        // Arrange
        UserRegistryDTO userRegistration = new UserRegistryDTO(
            "John Bakke",
            "john.bakke@gmail.com",
            "12345678",
            "!johnbakke",
            "!johnbakke"
        );

        // Act
        List<String> validationErrors = userValidator.validate(userRegistration);

        // Assert
        assertTrue(validationErrors.contains("Password must contain a number"));
        assertEquals(1, validationErrors.size());
    }

    @Test
    void validate_PasswordError_NoSpecialCharacter() {
        // Arrange
        UserRegistryDTO userRegistration = new UserRegistryDTO(
            "John Bakke",
            "john.bakke@gmail.com",
            "12345678",
            "johnbakke1",
            "johnbakke1"
        );

        // Act
        List<String> validationErrors = userValidator.validate(userRegistration);

        // Assert
        assertTrue(validationErrors.contains("Password must contain special character"));
        assertEquals(1, validationErrors.size());
    }

    @Test
    void validate_PasswordError_PasswordLength() {
        // Arrange
        UserRegistryDTO userRegistration = new UserRegistryDTO(
            "John Bakke",
            "john.bakke@gmail.com",
            "12345678",
            "!john1",
            "!john1"
        );

        // Act
        List<String> validationErrors = userValidator.validate(userRegistration);

        // Assert
        assertTrue(validationErrors.contains("Password must be between 8-20 characters"));
        assertEquals(1, validationErrors.size());
    }

    @Test
    void validate_MatchingPasswordsError() {
        // Arrange
        UserRegistryDTO userRegistration = new UserRegistryDTO(
            "John Bakke",
            "john.bakke@gmail.com",
            "12345678",
            "!johnbakke1",
            "!johnhul2"
        );

        // Act
        List<String> validationErrors = userValidator.validate(userRegistration);

        // Assert
        assertTrue(validationErrors.contains("Passwords must match"));
        assertEquals(1, validationErrors.size());
    }

    @Test
    void validate_EverythingWrong() {
        // Arrange
        UserRegistryDTO userRegistration = new UserRegistryDTO(
            "John",
            "john.bakke",
            "112",
            "john",
            "!johnhul2"
        );

        // Act
        List<String> validationErrors = userValidator.validate(userRegistration);

        // Assert
        assertEquals(5, validationErrors.size());
    }
}
