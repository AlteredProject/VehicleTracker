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

    public void createUser(UserRegistryDTO userToRegister) {
        //Validate
        List<String> errorMessages = userValidator.validate(userToRegister);
        if (!errorMessages.isEmpty()) {
            System.out.println(errorMessages);
            return;
        }

        User userToCreate = new User();

        userToCreate.setUsername(userToRegister.name());
        userToCreate.setEmail(userToRegister.email());
        userToCreate.setPhone(userToRegister.phone());

        //Encrypt
        String encryptedPassword = passwordEncrypter.hashPassword(userToRegister.password());
        userToCreate.setPassword(encryptedPassword);

        //Create
        userToCreate.setCreatedAt(LocalDate.now());
        userToCreate.setUpdatedAt(LocalDate.now());

        userDAO.create(userToCreate);
    }

    public void getUser(int id) {
        User user = userDAO.getById(id);
        System.out.println(user);

    }
}
