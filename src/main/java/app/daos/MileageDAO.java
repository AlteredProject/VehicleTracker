package app.daos;

import app.entities.MileageEntry;
import jakarta.persistence.EntityManagerFactory;

public class MileageDAO extends GenericDAO<MileageEntry> {
    // Generic CRUD methods
    public MileageDAO(EntityManagerFactory emf) {
        super(emf, MileageEntry.class, MileageEntry.class.getSimpleName());
    }
}
