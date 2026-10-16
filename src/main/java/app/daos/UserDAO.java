package app.daos;

import app.entities.User;
import app.exceptions.ApiException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;

public class UserDAO extends GenericDAO<User> {

    public UserDAO(EntityManagerFactory emf) {
        super(emf, User.class, User.class.getSimpleName());
    }

    public User getUserByName(String name) {
        if(name == null || name.isEmpty()) {
            throw new ApiException(400, "Username is required");
        }

        try (EntityManager em = emf.createEntityManager()) {
            try {
                TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class);
                query.setParameter("username", name);

                return query.getResultStream().findFirst().orElse(null);

            } catch (PersistenceException e) {
                throw new ApiException(500, "Get User failed: " + e.getMessage());
            }
        }
    }
}
