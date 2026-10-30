package app.services;

import app.daos.MaintenanceEntryDAO;
import app.daos.MileageEntryDAO;
import app.daos.VehicleDAO;
import app.dtos.entry.MaintenanceEntryRequestDTO;
import app.dtos.entry.MaintenanceEntryUpdateDTO;
import app.entities.MaintenanceEntry;
import app.entities.MileageEntry;
import app.entities.Vehicle;
import app.enums.MileageEntrySource;
import app.exceptions.BadRequestException;
import app.exceptions.EntityNotFoundException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

import java.time.LocalDate;

public class MaintenanceEntryService {
    private final EntityManagerFactory emf;
    private final MaintenanceEntryDAO maintenanceEntryDAO;
    private final MileageEntryDAO mileageEntryDAO;
    private final VehicleDAO vehicleDAO;

    public MaintenanceEntryService(EntityManagerFactory emf, MaintenanceEntryDAO maintenanceEntryDAO, MileageEntryDAO mileageEntryDAO, VehicleDAO vehicleDAO) {
        this.emf = emf;
        this.maintenanceEntryDAO = maintenanceEntryDAO;
        this.mileageEntryDAO = mileageEntryDAO;
        this.vehicleDAO = vehicleDAO;
    }

    public MaintenanceEntry createMaintenanceEntry(MaintenanceEntryRequestDTO dto, Long vehicleId, Integer userId) {
        if (dto == null || dto.date() == null) {
            throw new BadRequestException("Maintenance entry date is required");
        }

        if (dto.mileage() < 0 || dto.cost() < 0 || !Float.isFinite(dto.mileage()) || !Double.isFinite(dto.cost())) {
            throw new BadRequestException("Invalid maintenance entry values");
        }

        if (dto.description() == null || dto.description().isBlank()) {
            throw new BadRequestException("Description is required");
        }

        if (dto.description().length() > 255) {
            throw new BadRequestException("Description must not exceed 255 characters");
        }

        if (dto.category() == null) {
            throw new BadRequestException("Maintenance category is required");
        }

        if(vehicleId == null || userId == null) {
            throw new BadRequestException("Vehicle ID and user ID are required");
        }

        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction etx = em.getTransaction();
            etx.begin();

            try {
                Vehicle vehicle = vehicleDAO.getVehicleByIdAndUserId(em, vehicleId, userId);

                if (vehicle == null) {
                    throw new EntityNotFoundException("Vehicle not found");
                }

                LocalDate now = LocalDate.now();

                MaintenanceEntry maintenanceEntry = MaintenanceEntry.builder()
                        .date(dto.date())
                        .mileage(dto.mileage())
                        .category(dto.category())
                        .description(dto.description())
                        .cost(dto.cost())
                        .createdAt(now)
                        .updatedAt(now)
                        .vehicle(vehicle)
                        .build();

                MileageEntry mileageEntry = MileageEntry.builder()
                        .date(dto.date())
                        .mileage(dto.mileage())
                        .source(MileageEntrySource.MAINTENANCE_ENTRY)
                        .createdAt(now)
                        .updatedAt(now)
                        .vehicle(vehicle)
                        .build();

                maintenanceEntryDAO.createInTransaction(em, maintenanceEntry);
                mileageEntryDAO.createInTransaction(em, mileageEntry);

                etx.commit();
                return maintenanceEntry;
            } catch (RuntimeException e) {
                if (etx.isActive()) {
                    etx.rollback();
                }
                throw e;
            }
        }
    }

    public MaintenanceEntry updateMaintenanceEntry(MaintenanceEntryUpdateDTO dto, Long vehicleId, Long maintenanceEntryId, Integer userId) {
        if (dto == null) {
            throw new BadRequestException("Maintenance entry update data is required");
        }

        if (vehicleId == null || maintenanceEntryId == null || userId == null) {
            throw new BadRequestException("Required IDs are missing");
        }

        validateMaintenanceEntryUpdate(dto);

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            try {
                MaintenanceEntry maintenanceEntry = maintenanceEntryDAO.getByIdAndVehicleIdAndUserId(em, maintenanceEntryId, vehicleId, userId);

                if (maintenanceEntry == null) {
                    throw new EntityNotFoundException("Maintenance entry not found");
                }

                if (dto.date() != null) {
                    maintenanceEntry.setDate(dto.date());
                }

                if (dto.mileage() != null) {
                    maintenanceEntry.setMileage(dto.mileage());
                }

                if (dto.category() != null) {
                    maintenanceEntry.setCategory(dto.category());
                }

                if (dto.description() != null) {
                    maintenanceEntry.setDescription(dto.description());
                }

                if (dto.cost() != null) {
                    maintenanceEntry.setCost(dto.cost());
                }

                MileageEntry mileageEntry = maintenanceEntry.getMileageEntry();

                if (mileageEntry != null) {
                    if (dto.date() != null) {
                        mileageEntry.setDate(maintenanceEntry.getDate());
                    }

                    if (dto.mileage() != null) {
                        mileageEntry.setMileage(maintenanceEntry.getMileage());
                    }
                }

                maintenanceEntry.setUpdatedAt(LocalDate.now());

                em.getTransaction().commit();
                return  maintenanceEntry;
            } catch (RuntimeException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                throw e;
            }
        }
    }

    private void validateMaintenanceEntryUpdate(
            MaintenanceEntryUpdateDTO dto) {

        if (dto.mileage() != null &&
                (!Float.isFinite(dto.mileage()) || dto.mileage() < 0)) {
            throw new BadRequestException("Invalid mileage");
        }

        if (dto.cost() != null &&
                (!Double.isFinite(dto.cost()) || dto.cost() < 0)) {
            throw new BadRequestException("Invalid maintenance cost");
        }

        if (dto.description() != null && dto.description().isBlank()) {
            throw new BadRequestException("Description cannot be empty");
        }
    }
}
