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

        String email = userRegistryDTO.email();
        String phone = userRegistryDTO.phone();
        String password = userRegistryDTO.password();
        String passwordRepeat = userRegistryDTO.passwordRepeat();

        listOfValidationErrors.add(validateEmail(email));
        listOfValidationErrors.add(validatePhoneNumber(phone));
        listOfValidationErrors.add(validatePassword(password));
        listOfValidationErrors.add(validateMatching(password, passwordRepeat));

        return listOfValidationErrors;
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
            return "Not a valid phonenumber";
        }
        return null;
    }

    private String validatePassword(String password) {
        if(password.chars().noneMatch(Character::isDigit)) {
            return "Password must contain a number";
        }
        if(password.chars().noneMatch(Character::isLetterOrDigit)) {
            return "Password must contain special character";
        }
        String passwordRegex = "^[a-zA-Z0-9]{8,20}$";
        if(!password.matches(passwordRegex)) {
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
