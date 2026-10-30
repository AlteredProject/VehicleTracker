package app.daos;

import app.entities.FuelEntry;
import app.entities.Vehicle;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import java.time.LocalDate;
import java.util.List;


public class FuelEntryDAO extends GenericDAO<FuelEntry, Long> {
    // Generic CRUD methods
    public FuelEntryDAO(EntityManagerFactory emf) {
        super(emf, FuelEntry.class, FuelEntry.class.getSimpleName());
    }

    @Override
    protected Long getId(FuelEntry fuelEntry) {
        return fuelEntry.getEntryId();
    }

    public FuelEntry getByIdAndVehicleIdAndUserId(EntityManager em, Long fuelEntryId, Long vehicleId, Integer userId) {
        TypedQuery<FuelEntry> query = em.createQuery("SELECT f FROM FuelEntry f LEFT JOIN FETCH f.mileageEntry WHERE f.entryId = :fuelEntryId AND f.vehicle.vehicleId = :vehicleId AND f.vehicle.user.userId = :userId", FuelEntry.class);
        query.setParameter("fuelEntryId", fuelEntryId);
        query.setParameter("vehicleId", vehicleId);
        query.setParameter("userId", userId);

        return query.getResultStream().findFirst().orElse(null);
    }

    public List<FuelEntry> getByVehicleIdAndDateRange(Long vehicleId, LocalDate startDate, LocalDate endDate) {
        try (EntityManager em = emf.createEntityManager()) {
            String jpql = "SELECT f FROM FuelEntry f WHERE f.vehicle.vehicleId = :vehicleId AND f.date <= :endDate";

            if (startDate != null) {
                jpql += " AND f.date >= :startDate";
            }

            jpql += " ORDER BY f.date ASC";

            var query = em.createQuery(jpql, FuelEntry.class);
            query.setParameter("vehicleId", vehicleId);
            query.setParameter("endDate", endDate);

            if (startDate != null) {
                query.setParameter("startDate", startDate);
            }

            return query.getResultList();
        }

    }

}
