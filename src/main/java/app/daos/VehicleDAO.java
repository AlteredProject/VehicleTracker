package app.daos;

import app.entities.Vehicle;
import app.exceptions.ApiException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;

public class VehicleDAO extends GenericDAO<Vehicle> {
    // Generic CRUD methods
    public VehicleDAO(EntityManagerFactory emf) {
        super(emf, Vehicle.class, Vehicle.class.getSimpleName());
    }

    // Class specific methods
    public Vehicle getVehicleByRegistrationNumber(String registrationNumber) {
        if (registrationNumber == null || registrationNumber.isBlank()) {
            throw new ApiException(400, "Registration number is required");
        }

        try (EntityManager em = emf.createEntityManager()) {
            try {
                TypedQuery<Vehicle> query = em.createQuery("SELECT v FROM Vehicle v WHERE v.registrationNumber = :registrationNumber", Vehicle.class);
                query.setParameter("registrationNumber", registrationNumber);

                return query.getResultStream().findFirst().orElse(null);

            } catch (PersistenceException e) {
                throw new ApiException(500, "Get Vehicle failed: " + e.getMessage());
            }
        }
    }
}
