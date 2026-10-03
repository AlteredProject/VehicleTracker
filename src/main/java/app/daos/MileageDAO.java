package app.daos;

import app.entities.MileageEntry;
import app.exceptions.ApiException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class MileageDAO extends GenericDAO<MileageEntry> {
    // Generic CRUD methods
    public MileageDAO(EntityManagerFactory emf) {
        super(emf, MileageEntry.class, MileageEntry.class.getSimpleName());
    }
}
