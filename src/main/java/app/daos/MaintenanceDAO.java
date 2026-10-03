package app.daos;

import app.entities.MaintenanceEntry;
import app.exceptions.ApiException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class MaintenanceDAO extends GenericDAO<MaintenanceEntry> {
    // Generic CRUD methods
    public MaintenanceDAO(EntityManagerFactory emf) {
        super(emf, MaintenanceEntry.class, MaintenanceEntry.class.getSimpleName());
    }
}
