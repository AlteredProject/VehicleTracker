package app.services;

import app.daos.FuelDAO;
import app.daos.MileageDAO;
import app.entities.FuelEntry;
import app.entities.MileageEntry;

public class FuelEntryService {
    private final FuelDAO fuelDAO;

    public FuelEntryService(FuelDAO fuelDAO) {
        this.fuelDAO = fuelDAO;
    }

//    public FuelEntry addFuelEntry(int vehicleId, FuelEntryDTO fuelEntryDTO) {
//        FuelEntry fuelEntry = new FuelEntry();
//
//    }
}
