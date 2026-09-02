package app.daos;

import app.entities.MaintenanceEntry;
import app.exceptions.ApiException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class MaintenanceDAO implements IDAO<MaintenanceEntry, Integer> {
    private final EntityManagerFactory emf;

    public MaintenanceDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public MaintenanceEntry create(MaintenanceEntry maintenanceEntry) {

        if (maintenanceEntry == null) {
            throw new ApiException(400, "MaintenanceEntry id is required");
        }

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            try {
                em.persist(maintenanceEntry);
                em.getTransaction().commit();
            } catch (PersistenceException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                throw new ApiException(500, "Create MaintenanceEntry failed: " + e.getMessage());
            } catch (RuntimeException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                throw e;
            }
        }
        return maintenanceEntry;
    }

    @Override
    public MaintenanceEntry getById(Integer id) {

        if (id == null) {
            throw new ApiException(400, "MaintenanceEntry id is required");
        }

        try (EntityManager em = emf.createEntityManager()) {
            try {
                MaintenanceEntry maintenanceEntry = em.find(MaintenanceEntry.class, id);
                if (maintenanceEntry != null) {
                    return maintenanceEntry;
                }
                throw new ApiException(404, "MaintenanceEntry not found");
            } catch (PersistenceException e) {
                throw new ApiException(500, "Get MaintenanceEntry failed: " + e.getMessage());
            }
        }

    }

    @Override
    public List<MaintenanceEntry> getAll() {
        try (EntityManager em = emf.createEntityManager()) {
            try {
                TypedQuery<MaintenanceEntry> query = em.createQuery("SELECT m FROM MaintenanceEntry  m", MaintenanceEntry.class);
                return query.getResultList();
            } catch (PersistenceException e) {
                throw new ApiException(500, "GetMaintenanceEntries failed: " + e.getMessage());
            }
        }
    }

    @Override
    public MaintenanceEntry update(MaintenanceEntry maintenanceEntry) {

        if (maintenanceEntry == null || maintenanceEntry.getEntryId() == null) {
            throw new ApiException(400, "MaintenanceEntry id is required");
        }

        MaintenanceEntry updated;
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            try {
                MaintenanceEntry existing = em.find(MaintenanceEntry.class, maintenanceEntry.getEntryId());
                if (existing == null) {
                    throw new ApiException(404, "MaintenanceEntry not found");
                }
                updated = em.merge(maintenanceEntry);
                em.getTransaction().commit();
            } catch (PersistenceException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                throw new ApiException(500, "Update MaintenanceEntry failed: " + e.getMessage());
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
            throw new ApiException(400, "MaintenanceEntry id is required");
        }

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            try {
                MaintenanceEntry maintenanceEntryToRemove = em.find(MaintenanceEntry.class, id);
                if (maintenanceEntryToRemove != null) {
                    em.remove(maintenanceEntryToRemove);
                } else {
                    throw new ApiException(404, "MaintenanceEntry not found");
                }
                em.getTransaction().commit();
            } catch (PersistenceException e) {
                if(em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                throw new ApiException(500, "Delete MaintenanceEntry failed: " + e.getMessage());
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
