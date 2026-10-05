package app.daos;

import app.entities.User;
import jakarta.persistence.EntityManagerFactory;

public class UserDAO extends GenericDAO<User> {

    public UserDAO(EntityManagerFactory emf) {
        super(emf, User.class, User.class.getSimpleName());
    }
}
