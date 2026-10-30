package app.daos;

import app.entities.Vehicle;
import app.exceptions.BadRequestException;
import app.exceptions.DatabaseException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class VehicleDAO extends GenericDAO<Vehicle, Long> {
    // Generic CRUD methods
    public VehicleDAO(EntityManagerFactory emf) {
        super(emf, Vehicle.class, Vehicle.class.getSimpleName());
    }

    @Override
    protected Long getId(Vehicle vehicle) {
        return vehicle.getVehicleId();
    }

    // Class specific methods
    public Vehicle getVehicleByRegistrationNumber(String registrationNumber) {
        if (registrationNumber == null || registrationNumber.isBlank()) {
            throw new BadRequestException("Registration number is required");
        }

        try (EntityManager em = emf.createEntityManager()) {
            try {
                TypedQuery<Vehicle> query = em.createQuery("SELECT v FROM Vehicle v WHERE v.registrationNumber = :registrationNumber", Vehicle.class);
                query.setParameter("registrationNumber", registrationNumber);

                return query.getResultStream().findFirst().orElse(null);

            } catch (PersistenceException e) {
                throw new DatabaseException("Get Vehicle failed: ", e);
            }
        }
    }

    public Vehicle getVehicleByIdAndUserId(Long vehicleId, Integer userId) {
        try (EntityManager em = emf.createEntityManager()) {
            return getVehicleByIdAndUserId(em, vehicleId, userId);
        }
    }

    public Vehicle getVehicleByIdAndUserId(EntityManager em, Long vehicleId, Integer userId) {
        if (vehicleId == null || userId == null) {
            throw new BadRequestException("Both vehicleID and userID are required");
        }

        try {
            TypedQuery<Vehicle> query = em.createQuery("SELECT v FROM Vehicle v WHERE v.vehicleId = :vehicleId AND v.user.userId = :userId", Vehicle.class);
            query.setParameter("vehicleId", vehicleId);
            query.setParameter("userId", userId);

            return query.getResultStream().findFirst().orElse(null);
        } catch (PersistenceException e) {
            throw new DatabaseException("Failed to get vehicle by ID and user ID", e);
        }

    }

    public List<Vehicle> getAllVehiclesFromUser(Integer userId) {
        if (userId == null) {
            throw new BadRequestException("UserID is required");
        }

        try (EntityManager em = emf.createEntityManager()) {
            try {
                TypedQuery<Vehicle> query = em.createQuery("SELECT v FROM Vehicle v WHERE v.user.userId = :userId", Vehicle.class);
                query.setParameter("userId", userId);

                return query.getResultList();
            } catch (PersistenceException e) {
                throw new DatabaseException("Get all vehicles failed: ", e);
            }
        }
    }
}
