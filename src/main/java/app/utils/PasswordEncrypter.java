package app.utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordEncrypter {
    public String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public boolean checkPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }
}
