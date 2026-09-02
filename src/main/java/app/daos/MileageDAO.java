package app.daos;

import app.entities.MileageEntry;
import app.exceptions.ApiException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class MileageDAO implements IDAO<MileageEntry, Integer> {
    private final EntityManagerFactory emf;

    public MileageDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public MileageEntry create(MileageEntry mileageEntry) {
        if (mileageEntry == null) {
            throw new ApiException(400, "MileageEntry id is required");
        }

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            try {
                em.persist(mileageEntry);
                em.getTransaction().commit();
            } catch (PersistenceException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                throw new ApiException(500, "Create MileageEntry failed: " + e.getMessage());
            } catch (RuntimeException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                throw e;
            }
        }
        return mileageEntry;
    }

    @Override
    public MileageEntry getById(Integer id) {

        if (id == null) {
            throw new ApiException(400, "MileageEntry id is required");
        }

        try (EntityManager em = emf.createEntityManager()) {
            try {
                MileageEntry mileageEntry = em.find(MileageEntry.class, id);
                if (mileageEntry != null) {
                    return mileageEntry;
                }
                throw new ApiException(404, "MileageEntry not found");
            } catch (PersistenceException e) {
                throw new ApiException(500, "Get MileageEntry failed: " + e.getMessage());
            }
        }
    }

    @Override
    public List<MileageEntry> getAll() {
        try (EntityManager em = emf.createEntityManager()) {
            try {
                TypedQuery<MileageEntry> query = em.createQuery("SELECT m FROM MileageEntry m", MileageEntry.class);
                return query.getResultList();
            } catch (PersistenceException e) {
                throw new ApiException(500, "Get MileageEntries failed: " + e.getMessage());
            }
        }
    }

    @Override
    public MileageEntry update(MileageEntry mileageEntry) {

        if (mileageEntry == null || mileageEntry.getEntryId() == null) {
            throw new ApiException(400, "MileageEntry id is required");
        }

        MileageEntry updated;
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            try {
                MileageEntry existing = em.find(MileageEntry.class, mileageEntry.getEntryId());
                if (existing == null) {
                    throw new ApiException(404, "MileageEntry not found");
                }
                updated = em.merge(mileageEntry);
                em.getTransaction().commit();
            } catch (PersistenceException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                throw new ApiException(500, "Update MileageEntry failed: " + e.getMessage());
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
            throw new ApiException(400, "MileageEntry id is required");
        }

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            try {
                MileageEntry mileageEntryToRemove = em.find(MileageEntry.class, id);
                if (mileageEntryToRemove != null) {
                    em.remove(mileageEntryToRemove);
                } else {
                    throw new ApiException(404, "MileageEntry not found");
                }
                em.getTransaction().commit();
            } catch (PersistenceException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                throw new ApiException(500, "Delete MileageEntry failed: " + e.getMessage());
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
