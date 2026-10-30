package app.daos;

import app.exceptions.BadRequestException;
import app.exceptions.DatabaseException;
import app.exceptions.EntityNotFoundException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceException;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@AllArgsConstructor
public abstract class GenericDAO<T, I> implements IDAO<T, I> {
    protected EntityManagerFactory emf;
    private final Class<T> tClass;
    private String entityName;

    protected abstract I getId(T entity);

    @Override
    public T create(T entity) {
        if (entity == null) {
            throw new BadRequestException(entityName + " id is required");
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
                throw new DatabaseException("Create " + entityName + " failed: ", e);
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
    public T getById(I id) {
        if (id == null) {
            throw new BadRequestException(entityName + " id is required");
        }

        try (EntityManager em = emf.createEntityManager()) {
            try {
                T entity = em.find(tClass, id);
                if (entity != null) {
                    return entity;
                }
                throw new EntityNotFoundException(entityName + " not found");
            } catch (PersistenceException e) {
                throw new DatabaseException("Get " + entityName + " failed: ", e);
            }
        }
    }

    @Override
    public List<T> getAll() {
        try (EntityManager em = emf.createEntityManager()) {
            try {
                String query = "SELECT t FROM " + tClass.getSimpleName() + " t";
                return new ArrayList<>(em.createQuery(query, tClass).getResultList());
            } catch (PersistenceException e) {
                throw new DatabaseException("Getting all" + entityName + " failed: ", e);
            }
        }
    }

    @Override
    public T update(T entity) {

        if (entity == null) {
            throw new BadRequestException(entityName + " is required");
        }

        I id = getId(entity);

        if (id == null) {
            throw new BadRequestException(entityName + " ID is required");
        }

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            try {
                T existing = em.find(tClass, id);
                if (existing == null) {
                    throw new EntityNotFoundException(entityName + " not found");
                }
                T updated = em.merge(entity);
                em.getTransaction().commit();
                return updated;
            } catch (PersistenceException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                throw new DatabaseException("Update" + entityName + " failed: ", e);
            } catch (RuntimeException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                throw e;
            }
        }
    }

    @Override
    public boolean delete(I id) {

        if (id == null) {
            throw new BadRequestException(entityName + " id is required");
        }

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            try {
                T entityToRemove = em.find(tClass, id);
                if (entityToRemove != null) {
                    em.remove(entityToRemove);
                } else {
                    throw new EntityNotFoundException(entityName + " not found");
                }
                em.getTransaction().commit();
            } catch (PersistenceException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                throw new DatabaseException("Delete " + entityName + " failed: ", e);
            } catch (RuntimeException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                throw e;
            }
        }
        return true;
    }

    public T createInTransaction(EntityManager em, T entity) {
        if (entity == null) {
            throw new BadRequestException(entityName + " is required");
        }

        em.persist(entity);
        return entity;
    }

    public T updateInTransaction(EntityManager em, T entity) {
        if (entity == null) {
            throw new BadRequestException(entityName + " is required");
        }

        I id = getId(entity);

        if (id == null) {
            throw new BadRequestException(entityName + " ID is required");
        }

        T existing = em.find(tClass, id);

        if (existing == null) {
            throw new EntityNotFoundException(entityName + " not found");
        }

        return em.merge(entity);
    }
}
