package app.daos;

import app.exceptions.ApiException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceException;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@AllArgsConstructor
public abstract class GenericDAO<T> implements IDAO<T, Integer> {
    protected EntityManagerFactory emf;
    private final Class<T> tClass;
    private String entityName;

    @Override
    public T create(T entity) {
        if (entity == null) {
            throw new ApiException(400, entityName + " id is required");
        }

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            try {
                em.persist(entity);
                em.getTransaction().commit();
            } catch (PersistenceException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                throw new ApiException(500, "Create " + entityName + " failed: " + e.getMessage());
            } catch (RuntimeException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                throw e;
            }
        }
        return entity;
    }

    @Override
    public T getById(Integer id) {
        if (id == null) {
            throw new ApiException(400, entityName + " id is required");
        }

        try (EntityManager em = emf.createEntityManager()) {
            try {
                T entity = em.find(tClass, id);
                if (entity != null) {
                    return entity;
                }
                throw new ApiException(404, entityName + " not found");
            } catch (PersistenceException e) {
                throw new ApiException(500, "Get " + entityName + " failed: " + e.getMessage());
            }
        }
    }

    @Override
    public List<T> getAll() {
        try (EntityManager em = emf.createEntityManager()) {
            try {
                String query = "SELECT t FROM" + tClass.getSimpleName() + " t";
                return new ArrayList<>(em.createQuery(query, tClass).getResultList());
            } catch (PersistenceException e) {
                throw new ApiException(500, "Getting all" + entityName + " failed: " + e.getMessage());
            }
        }
    }

    @Override
    public T update(T entity) {

        if (entity == null) {
            throw new ApiException(400, entityName + " id is required");
        }

        T updated;
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            try {
                T existing = em.find(tClass, entity);
                if (existing == null) {
                    throw new ApiException(404, entityName + " not found");
                }
                updated = em.merge(entity);
                em.getTransaction().commit();
            } catch (PersistenceException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                throw new ApiException(500, "Update" + entityName + " failed: " + e.getMessage());
            } catch (RuntimeException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                throw e;
            }
        }
        return updated;
    }

    @Override
    public boolean delete(Integer id) {

        if (id == null) {
            throw new ApiException(400, entityName + " id is required");
        }

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            try {
                T entityToRemove = em.find(tClass, id);
                if (entityToRemove != null) {
                    em.remove(entityToRemove);
                } else {
                    throw new ApiException(404, entityName + " not found");
                }
                em.getTransaction().commit();
            } catch (PersistenceException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                throw new ApiException(500, "Delete " + entityName + " failed: " + e.getMessage());
            } catch (RuntimeException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                throw e;
            }
        }
        return true;
    }
}
