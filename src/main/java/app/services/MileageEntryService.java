package app.services;

import app.daos.MileageEntryDAO;
import app.daos.VehicleDAO;
import app.dtos.entry.MileageEntryRequestDTO;
import app.entities.MileageEntry;
import app.entities.Vehicle;
import app.enums.MileageEntrySource;
import app.exceptions.BadRequestException;
import app.exceptions.EntityNotFoundException;
import jakarta.persistence.EntityManagerFactory;

import java.time.LocalDate;

public class MileageEntryService {
    private final EntityManagerFactory emf;
    private final MileageEntryDAO mileageEntryDAO;
    private final VehicleDAO vehicleDAO;

    public MileageEntryService(EntityManagerFactory emf, MileageEntryDAO mileageEntryDAO, VehicleDAO vehicleDAO) {
        this.emf = emf;
        this.mileageEntryDAO = mileageEntryDAO;
        this.vehicleDAO = vehicleDAO;
    }

    public MileageEntry createMileageEntry(MileageEntryRequestDTO dto, Long vehicleId, Integer userId) {
        if (dto == null || dto.date() == null) {
            throw new BadRequestException("Mileage entry date is required");
        }

        if (dto.mileage() < 0) {
            throw new BadRequestException("Invalid mileage value");
        }

        if (vehicleId == null || userId == null) {
            throw new BadRequestException("Vehicle ID and user ID are required");
        }

        Vehicle vehicle = vehicleDAO.getVehicleByIdAndUserId(vehicleId, userId);

        if (vehicle == null) {
            throw new EntityNotFoundException("Vehicle not found");
        }

        LocalDate now = LocalDate.now();

        MileageEntry mileageEntry = MileageEntry.builder()
                .date(dto.date())
                .mileage(dto.mileage())
                .source(MileageEntrySource.MANUAL)
                .createdAt(now)
                .updatedAt(now)
                .vehicle(vehicle)
                .build();

        return mileageEntryDAO.create(mileageEntry);
    }
}

