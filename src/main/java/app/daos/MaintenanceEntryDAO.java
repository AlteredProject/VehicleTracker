package app.daos;

import app.entities.FuelEntry;
import app.entities.MaintenanceEntry;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import java.time.LocalDate;
import java.util.List;

public class MaintenanceEntryDAO extends GenericDAO<MaintenanceEntry, Long> {
    // Generic CRUD methods
    public MaintenanceEntryDAO(EntityManagerFactory emf) {
        super(emf, MaintenanceEntry.class, MaintenanceEntry.class.getSimpleName());
    }

    @Override
    protected Long getId(MaintenanceEntry maintenanceEntry) {
        return maintenanceEntry.getEntryId();
    }

    public MaintenanceEntry getByIdAndVehicleIdAndUserId(EntityManager em, Long maintenanceEntryId, Long vehicleId, Integer userId) {

        TypedQuery<MaintenanceEntry> query = em.createQuery(" SELECT m FROM MaintenanceEntry m LEFT JOIN FETCH m.mileageEntry WHERE m.entryId = :maintenanceEntryId AND m.vehicle.vehicleId = :vehicleId AND m.vehicle.user.userId = :userId", MaintenanceEntry.class);
        query.setParameter("maintenanceEntryId", maintenanceEntryId);
        query.setParameter("vehicleId", vehicleId);
        query.setParameter("userId", userId);

        return query.getResultStream().findFirst().orElse(null);
    }

    public List<MaintenanceEntry> getByVehicleIdAndDateRange(Long vehicleId, LocalDate startDate, LocalDate endDate) {
        try (EntityManager em = emf.createEntityManager()) {
            String jpql = "SELECT m FROM MaintenanceEntry m WHERE m.vehicle.vehicleId = :vehicleId AND m.date <= :endDate";

            if (startDate != null) {
                jpql += " AND m.date >= :startDate";
            }

            jpql += " ORDER BY m.date ASC";

            var query = em.createQuery(jpql, MaintenanceEntry.class);
            query.setParameter("vehicleId", vehicleId);
            query.setParameter("endDate", endDate);

            if (startDate != null) {
                query.setParameter("startDate", startDate);
            }

            return query.getResultList();
        }
    }
}
