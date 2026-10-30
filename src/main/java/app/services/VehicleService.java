    package app.services;

    import app.daos.MileageEntryDAO;
    import app.daos.UserDAO;
    import app.daos.VehicleDAO;
    import app.dtos.vehicle.ManualVehicleCreationDTO;
    import app.dtos.vehicle.MotorVehicleDTO;
    import app.dtos.vehicle.VehicleUpdateDTO;
    import app.entities.MileageEntry;
    import app.entities.User;
    import app.entities.Vehicle;
    import app.dtos.vehicle.VehicleCreationResultDTO;
    import app.enums.MileageEntrySource;
    import app.exceptions.BadRequestException;
    import app.exceptions.ConflictException;
    import app.exceptions.EntityNotFoundException;
    import jakarta.persistence.EntityManager;
    import jakarta.persistence.EntityManagerFactory;

    import java.time.LocalDate;
    import java.util.List;

    public class VehicleService {
        private final EntityManagerFactory emf;
        private final VehicleDAO vehicleDAO;
        private final UserDAO userDAO;
        private final MileageEntryDAO mileageEntryDAO;
        private final MotorAPIService motorAPIService;

        public VehicleService(EntityManagerFactory emf, VehicleDAO vehicleDAO, UserDAO userDAO, MileageEntryDAO mileageEntryDAO, MotorAPIService motorAPIService) {
            this.emf = emf;
            this.vehicleDAO = vehicleDAO;
            this.userDAO = userDAO;
            this.mileageEntryDAO = mileageEntryDAO;
            this.motorAPIService = motorAPIService;
        }

        public VehicleCreationResultDTO createVehicle(MotorVehicleDTO vehicleDTO, Integer userId) {
            User user = userDAO.getById(userId);
            Vehicle existingVehicle = vehicleDAO.getVehicleByRegistrationNumber(vehicleDTO.getRegistrationNumber());

            if (existingVehicle != null) {
                throw new ConflictException("A vehicle with this registration number already exists"    );
            }

            Vehicle vehicle = new Vehicle();

            vehicle.setRegistrationNumber(vehicleDTO.getRegistrationNumber());
            vehicle.setStatus(vehicleDTO.getStatus());
            vehicle.setVin(vehicleDTO.getVin());
            vehicle.setType(vehicleDTO.getType());
            vehicle.setMake(vehicleDTO.getMake());
            vehicle.setModel(vehicleDTO.getModel());
            vehicle.setVariant(vehicleDTO.getVariant());
            vehicle.setChassisType(vehicleDTO.getChassisType());
            vehicle.setModelYear(vehicleDTO.getModelYear());
            vehicle.setColor(vehicleDTO.getColor());
            vehicle.setFuelType(vehicleDTO.getFuelType());
            vehicle.setEngineVolume(vehicleDTO.getEngineVolume());
            vehicle.setEnginePower(vehicleDTO.getEnginePower());

            vehicle.setUser(user);

            vehicle.setCreatedAt(LocalDate.now());
            vehicle.setUpdatedAt(LocalDate.now());

            vehicleDAO.create(vehicle);
            return new VehicleCreationResultDTO(true, "Vehicle Successfully created");
        }

        public Vehicle getVehicle(Long id, Integer userId) {
            return vehicleDAO.getVehicleByIdAndUserId(id, userId);
        }

        public List<Vehicle> getAllVehiclesFromUser(Integer userId) {
            User user = userDAO.getById(userId);
            return vehicleDAO.getAllVehiclesFromUser(user.getUserId());
        }

        public MotorVehicleDTO lookupVehicle(String registrationNumber) {
            if (registrationNumber == null || registrationNumber.isBlank()) {
                throw new BadRequestException("Registration number is required");
            }

            return motorAPIService.getVehicle(registrationNumber.trim());
        }

        public VehicleCreationResultDTO createManualVehicle(ManualVehicleCreationDTO dto, Integer userId) {
            if(dto == null) {
                throw new BadRequestException("Vehicle details are required");
            }

            if(userId == null) {
                throw new BadRequestException("User ID is required");
            }

            if(dto.registrationNumber() == null || dto.registrationNumber().isBlank()) {
                throw new BadRequestException("Registration number is required");
            }

            if (dto.make() == null || dto.make().isBlank()) {
                throw new BadRequestException("Vehicle make is required");
            }

            if (dto.model() == null || dto.model().isBlank()) {
                throw new BadRequestException("Vehicle model is required");
            }

            if (dto.modelYear() != null && (dto.modelYear() < 1886 || dto.modelYear() > LocalDate.now().getYear() + 1)) {
                throw new BadRequestException("Invalid model year");
            }

            if (dto.mileage() != null && (!Float.isFinite(dto.mileage()) || dto.mileage() < 0)) {
                throw new BadRequestException("Mileage cannot be negative or invalid");
            }

            if (dto.engineVolume() != null && (!Double.isFinite(dto.engineVolume()) || dto.engineVolume() < 0)) {
                throw new BadRequestException("Engine volume cannot be negative or invalid");
            }

            if (dto.enginePower() != null && (!Double.isFinite(dto.enginePower()) || dto.enginePower() < 0)) {
                throw new BadRequestException("Engine power cannot be negative or invalid");
            }

            String registrationNumber = dto.registrationNumber().trim().toUpperCase();

            Vehicle existingVehicle = vehicleDAO.getVehicleByRegistrationNumber(registrationNumber);

            if (existingVehicle != null) {
                throw new ConflictException("A vehicle with this registration number already exists");
            }

            User user = userDAO.getById(userId);

            if (user == null) {
                throw new EntityNotFoundException("User not found");
            }

            Vehicle vehicle = new Vehicle();

            vehicle.setRegistrationNumber(registrationNumber);
            vehicle.setType(dto.type());
            vehicle.setMake(dto.make().trim());
            vehicle.setModel(dto.model().trim());
            vehicle.setModelYear(dto.modelYear());
            vehicle.setColor(dto.color());
            vehicle.setFuelType(dto.fuelType());
            vehicle.setVin(dto.vin());
            vehicle.setVariant(dto.variant());
            vehicle.setChassisType(dto.chassisType());
            vehicle.setEngineVolume(dto.engineVolume());
            vehicle.setEnginePower(dto.enginePower());
            vehicle.setMileage(dto.mileage());

            vehicle.setUser(user);
            vehicle.setCreatedAt(LocalDate.now());
            vehicle.setUpdatedAt(LocalDate.now());

            vehicleDAO.create(vehicle);

            return new VehicleCreationResultDTO(true, "Vehicle successfully created");
        }

        public Vehicle updateVehicle(VehicleUpdateDTO dto, Long vehicleId, Integer userId) {
            if (dto == null) {
                throw new BadRequestException("Vehicle update data is required");
            }

            if (vehicleId == null || userId == null) {
                throw new BadRequestException("Vehicle ID and user ID are required");
            }

            Vehicle vehicle = vehicleDAO.getVehicleByIdAndUserId(vehicleId, userId);

            if (vehicle == null) {
                throw new EntityNotFoundException("Vehicle not found");
            }

            validateVehicleUpdate(dto, vehicle);

            boolean mileageUpdated = dto.mileage() != null;

            if (dto.type() != null) vehicle.setType(dto.type());
            if (dto.make() != null) vehicle.setMake(dto.make());
            if (dto.model() != null) vehicle.setModel(dto.model());
            if (dto.variant() != null) vehicle.setVariant(dto.variant());
            if (dto.chassisType() != null) vehicle.setChassisType(dto.chassisType());
            if (dto.modelYear() != null) vehicle.setModelYear(dto.modelYear());
            if (dto.color() != null) vehicle.setColor(dto.color());
            if (dto.fuelType() != null) vehicle.setFuelType(dto.fuelType());
            if (dto.engineVolume() != null) vehicle.setEngineVolume(dto.engineVolume());
            if (dto.enginePower() != null) vehicle.setEnginePower(dto.enginePower());

            vehicle.setUpdatedAt(LocalDate.now());

            try (EntityManager em = emf.createEntityManager()) {
                em.getTransaction().begin();

                try {

                    if (mileageUpdated) {
                        vehicle.setMileage(dto.mileage());
                    }
                    Vehicle updatedVehicle = vehicleDAO.updateInTransaction(em, vehicle);

                    if (mileageUpdated) {
                    MileageEntry mileageEntry = new MileageEntry();
                        mileageEntry.setMileage(dto.mileage());
                        mileageEntry.setDate(LocalDate.now());
                        mileageEntry.setSource(MileageEntrySource.MANUAL);
                        mileageEntry.setVehicle(updatedVehicle);
                        mileageEntry.setCreatedAt(LocalDate.now());
                        mileageEntry.setUpdatedAt(LocalDate.now());

                        mileageEntryDAO.createInTransaction(em, mileageEntry);
                    }
                    em.getTransaction().commit();

                    return updatedVehicle;
                } catch (RuntimeException e) {
                    if (em.getTransaction().isActive()) {
                        em.getTransaction().rollback();
                    }
                    throw e;
                }
            }
        }

        private void validateVehicleUpdate(VehicleUpdateDTO dto, Vehicle vehicle) {

            if (dto.modelYear() != null && (dto.modelYear() < 1886 || dto.modelYear() > LocalDate.now().getYear() + 1)) {
                throw new BadRequestException("Invalid model year");
            }

            if (dto.mileage() != null && (!Float.isFinite(dto.mileage()) || dto.mileage() < 0)) {
                throw new BadRequestException("Invalid mileage");
            }

            if (dto.mileage() != null && vehicle.getMileage() != null && dto.mileage() < vehicle.getMileage()) {
                throw new BadRequestException("New mileage cannot be lower than current mileage");
            }

            if (dto.engineVolume() != null && (!Double.isFinite(dto.engineVolume()) || dto.engineVolume() < 0)) {
                throw new BadRequestException("Invalid engine volume");
            }

            if (dto.enginePower() != null && (!Double.isFinite(dto.enginePower()) || dto.enginePower() < 0)) {
                throw new BadRequestException("Invalid engine power");
            }
        }
    }
