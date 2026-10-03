package app.daos;

import app.entities.FuelEntry;
import jakarta.persistence.EntityManagerFactory;


public class FuelDAO extends GenericDAO<FuelEntry> {
    // Generic CRUD methods
    public FuelDAO(EntityManagerFactory emf) {
        super(emf, FuelEntry.class, FuelEntry.class.getSimpleName());
    }

}
