package app.tests.services;

import app.config.HibernateTestConfig;
import app.daos.UserDAO;
import app.dtos.UserRegistryDTO;
import app.entities.User;
import app.services.UserService;
import app.testutils.TestDatabasePopulator;
import app.dtos.LoginResultDTO;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserServiceTests {
    private final EntityManagerFactory emf = HibernateTestConfig.getEntityManagerFactory();

    private Map<String, User> userMap;

    private UserDAO userDAO;
    private UserService userService;

    @BeforeEach
    void beforeEach() {
        userMap = TestDatabasePopulator.createUsers(emf);
        userDAO = new UserDAO(emf);
        userService = new UserService(userDAO);
    }

    @AfterAll
    void shutdown() {
        emf.close();
    }

    // === USER CREATION ===
    @Test
    void createUser() {
        // Arrange
        UserRegistryDTO registration = new UserRegistryDTO(
            "John Bakke",
            "john.bakke@gmail.com",
            "21282224",
            "!johnbakke1",
            "!johnbakke1"
        );

        // Act
        List<String> errors = userService.createUser(registration);

        // Assert
        assertTrue(errors.isEmpty());
        assertTrue(userDAO.emailExists("john.bakke@gmail.com"));
    }

    // === USER LOGIN ===
    @Test
    void loginWithNonExistingUsername() {
        // Arrange
        String username = "I Dont Exist";
        String password = "!johnbakke1";

        // Act
        LoginResultDTO result = userService.login(username, password);

        // Assert
        assertEquals("Invalid username or password.", result.message());
    }

    @Test
    void loginWithCorrectPassword() {
        // Arrange
        String username = "John";
        String password = "!johnbakke1";

        // Act
        LoginResultDTO result = userService.login(username, password);

        // Assert
        assertTrue(result.success());
        assertEquals("Successfully Logged In", result.message());
    }

    @Test
    void loginWithWrongPassword() {
        // Arrange
        String username = "John";
        String password = "wrongPassword";

        // Act
        LoginResultDTO result = userService.login(username, password);

        // Assert
        assertEquals("Invalid username or password.", result.message());
    }

    // === USER FETCHING ===
    @Test
    void getUser() {
        // Arrange
        User user1 = userMap.get("user1");

        // Act
        User fetchedUser = userService.getUser(1);

        // Assert
        assertAll(
            () -> assertEquals(user1.getUserId(), fetchedUser.getUserId()),
            () -> assertEquals(user1.getUsername(), fetchedUser.getUsername()),
            () -> assertEquals(user1.getEmail(), fetchedUser.getEmail()),
            () -> assertEquals(user1.getPhone(), fetchedUser.getPhone())
        );
    }
}
