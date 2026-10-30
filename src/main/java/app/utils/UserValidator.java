package app.utils;

import app.dtos.UserRegistryDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserValidator {

    public List<String> validate(UserRegistryDTO userRegistryDTO) {
        List<String> listOfValidationErrors = new ArrayList<>();

        if (userRegistryDTO == null) {
            listOfValidationErrors.add("All required fields must be filled");
            return listOfValidationErrors;
        }

        String name = userRegistryDTO.username();
        String email = userRegistryDTO.email();
        String phone = userRegistryDTO.phone();
        String password = userRegistryDTO.password();
        String passwordRepeat = userRegistryDTO.passwordRepeat();

        addErrorIfPresent(listOfValidationErrors,validateName(name));
        addErrorIfPresent(listOfValidationErrors, validateEmail(email));
        addErrorIfPresent(listOfValidationErrors, validatePhoneNumber(phone));
        addErrorIfPresent(listOfValidationErrors, validatePassword(password));
        addErrorIfPresent(listOfValidationErrors, validateMatching(password, passwordRepeat));

        return listOfValidationErrors;
    }

    private void addErrorIfPresent(List<String> errorsList, String error) {
        if (error != null) {
            errorsList.add(error);
        }
    }

    private String validateName(String name) {
        if(name.length() < 6 || name.length() > 20) {
            return "Name must be between 6 and 20 characters";
        }
        return null;
    }

    private String validateEmail(String email) {
        String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        if(!email.matches(emailRegex)) {
            return "Not a valid email";
        }
        return null;
    }

    private String validatePhoneNumber(String phoneNumber) {
        String phoneNumRegex = "^[0-9]{8}$";
        if(!phoneNumber.matches(phoneNumRegex)) {
            return "Not a valid phone number";
        }
        return null;
    }

    private String validatePassword(String password) {
        if(password.chars().noneMatch(Character::isDigit)) {
            return "Password must contain a number";
        }
        if(password.chars().allMatch(Character::isLetterOrDigit)) {
            return "Password must contain special character";
        }
        if(password.length() < 8 || password.length() > 20) {
            return "Password must be between 8-20 characters";
        }
        return null;
    }

    private String validateMatching(String password, String passwordRepeat) {
        if(!Objects.equals(password, passwordRepeat)) {
            return "Passwords must match";
        }
        return null;
    }
}
