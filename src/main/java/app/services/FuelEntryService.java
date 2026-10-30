package app.services;

import app.daos.FuelEntryDAO;
import app.daos.MileageEntryDAO;
import app.daos.VehicleDAO;
import app.dtos.entry.FuelEntryRequestDTO;
import app.dtos.entry.FuelEntryUpdateDTO;
import app.entities.FuelEntry;
import app.entities.MileageEntry;
import app.entities.Vehicle;
import app.enums.MileageEntrySource;
import app.exceptions.BadRequestException;
import app.exceptions.EntityNotFoundException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

import java.time.LocalDate;

public class FuelEntryService {
    private final EntityManagerFactory emf;
    private final FuelEntryDAO fuelEntryDAO;
    private final MileageEntryDAO mileageEntryDAO;
    private final VehicleDAO vehicleDAO;

    public FuelEntryService(EntityManagerFactory emf, FuelEntryDAO fuelEntryDAO, MileageEntryDAO mileageEntryDAO, VehicleDAO vehicleDAO) {
        this.emf = emf;
        this.fuelEntryDAO = fuelEntryDAO;
        this.mileageEntryDAO = mileageEntryDAO;
        this.vehicleDAO = vehicleDAO;
    }

    public FuelEntry createFuelEntry(FuelEntryRequestDTO dto, Long vehicleId, Integer userId) {
        if(dto == null || dto.date() == null) {
            throw new BadRequestException("Fuel entry date is required");
        }

        if(dto.mileage() < 0 || dto.liters() <= 0 || dto.price() < 0) {
            throw new BadRequestException("Invalid fuel entry values");
        }

        if(vehicleId == null || userId == null) {
            throw new BadRequestException("Vehicle ID and user ID are required");
        }

        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction etx = em.getTransaction();
            etx.begin();

            try {
                Vehicle vehicle = vehicleDAO.getVehicleByIdAndUserId(em, vehicleId, userId);

                if(vehicle == null) {
                    throw new EntityNotFoundException("Vehicle not found");
                }

                LocalDate now = LocalDate.now();

                FuelEntry fuelEntry = FuelEntry.builder()
                        .date(dto.date())
                        .mileage(dto.mileage())
                        .liters(dto.liters())
                        .price(dto.price())
                        .vehicle(vehicle)
                        .createdAt(now)
                        .updatedAt(now)
                        .build();

                MileageEntry mileageEntry = MileageEntry.builder()
                        .date(dto.date())
                        .mileage(dto.mileage())
                        .source(MileageEntrySource.FUEL_ENTRY)
                        .createdAt(now)
                        .updatedAt(now)
                        .vehicle(vehicle)
                        .build();

                fuelEntryDAO.createInTransaction(em, fuelEntry);
                mileageEntryDAO.createInTransaction(em, mileageEntry);

                etx.commit();
                return fuelEntry;
            } catch (RuntimeException e) {
                if (etx.isActive()) {
                    etx.rollback();
                }
                throw e;
            }
        }
    }

    public FuelEntry updateFuelEntry(FuelEntryUpdateDTO dto, Long vehicleId, Long fuelEntryId, Integer userId) {
        if (dto == null) {
            throw new BadRequestException("Fuel entry update data is required");
        }

        if (vehicleId == null || fuelEntryId == null || userId == null) {
            throw new BadRequestException("Required IDs are missing");
        }

        validateFuelEntryUpdate(dto);

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            try {
                FuelEntry fuelEntry = fuelEntryDAO.getByIdAndVehicleIdAndUserId(em, fuelEntryId, vehicleId, userId);

                if (fuelEntry == null) {
                    throw new EntityNotFoundException("Fuel entry not found");
                }

                if (dto.date() != null) {
                    fuelEntry.setDate(dto.date());
                }

                if (dto.mileage() != null) {
                    fuelEntry.setMileage(dto.mileage());
                }

                MileageEntry mileageEntry = fuelEntry.getMileageEntry();

                if (mileageEntry != null) {
                    if (dto.date() != null) {
                        mileageEntry.setDate(fuelEntry.getDate());
                    }

                    if (dto.mileage() != null) {
                        mileageEntry.setMileage(fuelEntry.getMileage());
                    }
                }

                if (dto.liters() != null) {
                    fuelEntry.setLiters(dto.liters());
                }

                if (dto.price() != null) {
                    fuelEntry.setPrice(dto.price());
                }

                fuelEntry.setUpdatedAt(LocalDate.now());

                em.getTransaction().commit();
                return fuelEntry;
            } catch (RuntimeException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                throw e;
            }
        }
    }

    private void validateFuelEntryUpdate(FuelEntryUpdateDTO dto) {
        if (dto.mileage() != null &&
                (!Float.isFinite(dto.mileage()) || dto.mileage() < 0)) {
            throw new BadRequestException("Invalid mileage");
        }

        if (dto.liters() != null &&
                (!Double.isFinite(dto.liters()) || dto.liters() <= 0)) {
            throw new BadRequestException("Invalid fuel amount");
        }

        if (dto.price() != null &&
                (!Double.isFinite(dto.price()) || dto.price() < 0)) {
            throw new BadRequestException("Invalid fuel price");
        }
    }
}
