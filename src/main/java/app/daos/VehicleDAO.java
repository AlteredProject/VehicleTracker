package app.daos;

import app.entities.Vehicle;
import app.exceptions.ApiException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class VehicleDAO implements IDAO<Vehicle, Integer> {
    private final EntityManagerFactory emf;

    public VehicleDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public Vehicle create(Vehicle vehicle) {

        if (vehicle == null) {
            throw new ApiException(400, "Vehicle id is required");
        }

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            try {
                em.persist(vehicle);
                em.getTransaction().commit();
            } catch (PersistenceException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                throw new ApiException(500, "Create Vehicle failed: " + e.getMessage());
            } catch (RuntimeException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                throw e;
            }
        }
        return vehicle;
    }

    @Override
    public Vehicle getById(Integer id) {

        if (id == null) {
            throw new ApiException(400, "Vehicle id is required");
        }

        try (EntityManager em = emf.createEntityManager()) {
            try {
                Vehicle vehicle = em.find(Vehicle.class, id);
                if (vehicle != null) {
                    return vehicle;
                }
                throw new ApiException(404, "Vehicle not found");
            } catch (PersistenceException e) {
                throw new ApiException(500, "Get Vehicle failed: " + e.getMessage());
            }
        }
    }

    @Override
    public List<Vehicle> getAll() {
        try (EntityManager em = emf.createEntityManager()) {
            try {
                TypedQuery<Vehicle> query = em.createQuery("SELECT v FROM Vehicle v", Vehicle.class);
                return query.getResultList();
            } catch (PersistenceException e) {
                throw new ApiException(500, "Get Vehicles failed: " + e.getMessage());
            }
        }
    }

    @Override
    public Vehicle update(Vehicle vehicle) {

        if (vehicle == null || vehicle.getVehicleId() == null) {
            throw new ApiException(400, "Vehicle id is required");
        }

        Vehicle updated;
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            try {
                Vehicle existing = em.find(Vehicle.class, vehicle.getVehicleId());
                if (existing == null) {
                    throw new ApiException(404, "Vehicle not found");
                }
                updated = em.merge(vehicle);
                em.getTransaction().commit();
            } catch (PersistenceException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                throw new ApiException(500, "Update Vehicle failed: " + e.getMessage());
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
            throw new ApiException(400, "Vehicle id is required");
        }

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            try {
                Vehicle vehicleToRemove = em.find(Vehicle.class, id);
                if (vehicleToRemove != null) {
                    em.remove(vehicleToRemove);
                } else {
                    throw new ApiException(404, "Vehicle not found");
                }
                em.getTransaction().commit();
            } catch (PersistenceException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                throw new ApiException(500, "Delete Vehicle failed: " + e.getMessage());
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
