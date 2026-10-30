package app.daos;

import app.entities.MileageEntry;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class MileageEntryDAO extends GenericDAO<MileageEntry, Long> {
    // Generic CRUD methods
    public MileageEntryDAO(EntityManagerFactory emf) {
        super(emf, MileageEntry.class, MileageEntry.class.getSimpleName());
    }

    @Override
    protected Long getId(MileageEntry mileageEntry) {
        return mileageEntry.getEntryId();
    }

    public List<MileageEntry> getAllByVehicleId(Long vehicleId) {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<MileageEntry> query = em.createQuery(" SELECT m FROM MileageEntry m WHERE m.vehicle.vehicleId = :vehicleId ORDER BY m.date ASC", MileageEntry.class);
            query.setParameter("vehicleId", vehicleId);

            return query.getResultList();
        }
    }
}
