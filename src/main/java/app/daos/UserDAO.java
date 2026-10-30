package app.daos;

import app.entities.User;
import app.exceptions.BadRequestException;
import app.exceptions.DatabaseException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;

public class UserDAO extends GenericDAO<User, Integer> {

    public UserDAO(EntityManagerFactory emf) {
        super(emf, User.class, User.class.getSimpleName());
    }

    @Override
    protected Integer getId(User user) {
        return user.getUserId();
    }

    public User getUserByName(String name) {
        if(name == null || name.isEmpty()) {
            throw new BadRequestException("Username is required");
        }

        try (EntityManager em = emf.createEntityManager()) {
            try {
                TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class);
                query.setParameter("username", name);

                return query.getResultStream().findFirst().orElse(null);

            } catch (PersistenceException e) {
                throw new DatabaseException("Get User failed: ", e);
            }
        }
    }

    public boolean emailExists(String email) {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Long> query = em.createQuery("SELECT COUNT(u) FROM User u WHERE u.email = :email", Long.class);
            query.setParameter("email", email);

            return query.getSingleResult() > 0;
        }
    }
}
