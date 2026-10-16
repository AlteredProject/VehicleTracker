package app.services;

import app.daos.UserDAO;
import app.dtos.UserRegistryDTO;
import app.entities.User;
import app.utils.PasswordEncrypter;
import app.utils.UserValidator;

import java.time.LocalDate;
import java.util.List;

public class UserService {
    private final UserDAO userDAO;
    private final UserValidator userValidator = new UserValidator();
    private final PasswordEncrypter passwordEncrypter = new PasswordEncrypter();

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public List<String> createUser(UserRegistryDTO userToRegister) {
        // Validate
        List<String> errorMessages = userValidator.validate(userToRegister);

        if (!errorMessages.isEmpty()) {
            return errorMessages;
        }

        // Check existing user
        if (userDAO.emailExists(userToRegister.email())) {
            errorMessages.add("An account with this email already exists");
            return errorMessages;
        }

        // Start creation
        User userToCreate = new User();

        userToCreate.setUsername(userToRegister.name());
        userToCreate.setEmail(userToRegister.email());
        userToCreate.setPhone(userToRegister.phone());

        // Encrypt
        String encryptedPassword = passwordEncrypter.hashPassword(userToRegister.password());
        userToCreate.setPassword(encryptedPassword);

        // Finish creation
        userToCreate.setCreatedAt(LocalDate.now());
        userToCreate.setUpdatedAt(LocalDate.now());

        userDAO.create(userToCreate);

        return errorMessages;
    }

    public void login(String username, String password) {
        // Check DB for user
        User user = userDAO.getUserByName(username);
        if (user == null) {
            System.out.println("No user with this username found.");
            return;
        }

        // Check password
        if(passwordEncrypter.checkPassword(password, user.getPassword())) {
            System.out.println("Successfully Logged In");
            return;
        } else {
            System.out.println("Password is incorrect, try again.");
        }
    }

    public void getUser(int id) {
        User user = userDAO.getById(id);
        System.out.println(user);

    }
}
