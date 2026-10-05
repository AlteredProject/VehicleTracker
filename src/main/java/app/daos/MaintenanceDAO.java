package app.daos;

import app.entities.MaintenanceEntry;
import jakarta.persistence.EntityManagerFactory;

public class MaintenanceDAO extends GenericDAO<MaintenanceEntry> {
    // Generic CRUD methods
    public MaintenanceDAO(EntityManagerFactory emf) {
        super(emf, MaintenanceEntry.class, MaintenanceEntry.class.getSimpleName());
    }
}
