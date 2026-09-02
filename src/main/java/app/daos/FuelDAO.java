package app.daos;

import app.entities.FuelEntry;
import app.exceptions.ApiException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class FuelDAO implements IDAO<FuelEntry, Integer> {
    private final EntityManagerFactory emf;

    public FuelDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public FuelEntry create(FuelEntry fuelEntry) {

        if (fuelEntry == null) {
            throw new ApiException(400, "FuelEntry id is required");
        }

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            try {
                em.persist(fuelEntry);
                em.getTransaction().commit();
            } catch (PersistenceException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                throw new ApiException(500, "Create FuelEntry failed: " + e.getMessage());
            } catch (RuntimeException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                throw e;
            }
        }
        return fuelEntry;
    }

    @Override
    public FuelEntry getById(Integer id) {

        if (id == null) {
            throw new ApiException(400, "FuelEntry id is required");
        }

        try (EntityManager em = emf.createEntityManager()) {
            try {
                FuelEntry fuelEntry = em.find(FuelEntry.class, id);
                if (fuelEntry != null) {
                    return fuelEntry;
                }
                throw new ApiException(404, "FuelEntry not found");
            } catch (PersistenceException e) {
                throw new ApiException(500, "Get FuelEntry failed: " + e.getMessage());
            }
        }
    }

    @Override
    public List<FuelEntry> getAll() {
        try (EntityManager em = emf.createEntityManager()) {
            try {
                TypedQuery<FuelEntry> query = em.createQuery("SELECT f FROM FuelEntry f", FuelEntry.class);
                return query.getResultList();
            } catch (PersistenceException e) {
                throw new ApiException(500, "Get FuelEntries failed: " + e.getMessage());
            }
        }
    }

    @Override
    public FuelEntry update(FuelEntry fuelEntry) {

        if (fuelEntry == null || fuelEntry.getEntryId() == null) {
            throw new ApiException(400, "FuelEntry id is required");
        }

        FuelEntry updated;
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            try {
                FuelEntry existing = em.find(FuelEntry.class, fuelEntry.getEntryId());
                if (existing == null) {
                    throw new ApiException(404, "FuelEntry not found");
                }
                updated = em.merge(fuelEntry);
                em.getTransaction().commit();
            } catch (PersistenceException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                throw new ApiException(500, "Update FuelEntry failed: " + e.getMessage());
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
            throw new ApiException(400, "FuelEntry id is required");
        }

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            try {
                FuelEntry fuelEntryToRemove = em.find(FuelEntry.class, id);
                if (fuelEntryToRemove != null) {
                    em.remove(fuelEntryToRemove);
                } else {
                    throw new ApiException(404, "FuelEntry not found");
                }
                em.getTransaction().commit();
            } catch (PersistenceException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                throw new ApiException(500, "Delete FuelEntry failed: " + e.getMessage());
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
