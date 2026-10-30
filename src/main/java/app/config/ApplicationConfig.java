package app.config;

import app.controllers.*;
import app.controllers.entry.*;
import app.daos.*;
import app.exceptions.*;
import app.security.JWTMiddleware;
import app.services.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.javalin.Javalin;
import io.javalin.apibuilder.EndpointGroup;
import io.javalin.http.HttpStatus;
import io.javalin.json.JavalinJackson;
import jakarta.persistence.EntityManagerFactory;

import java.util.Map;

public class ApplicationConfig implements EndpointGroup {
    private final JWTMiddleware jwtMiddleware;
    private final UserController userController;
    private final VehicleController vehicleController;
    private final FuelEntryController fuelEntryController;
    private final MaintenanceEntryController maintenanceEntryController;
    private final MileageEntryController mileageEntryController;
    private final StatisticsController statisticsController;

    public ApplicationConfig(EntityManagerFactory emf) {
        // === DAOS ===
        FuelEntryDAO fuelEntryDAO = new FuelEntryDAO(emf);
        MaintenanceEntryDAO maintenanceEntryDAO = new MaintenanceEntryDAO(emf);
        MileageEntryDAO mileageEntryDAO = new MileageEntryDAO(emf);
        UserDAO userDAO = new UserDAO(emf);
        VehicleDAO vehicleDAO = new VehicleDAO(emf);

        // === SERVICES ===
        MotorAPIService motorAPIService = new MotorAPIService();
        UserService userService = new UserService(userDAO);
        VehicleService vehicleService = new VehicleService(emf, vehicleDAO, userDAO, mileageEntryDAO, motorAPIService);
        JWTService jwtService = new JWTService();
        FuelEntryService fuelEntryService = new FuelEntryService(emf, fuelEntryDAO, mileageEntryDAO, vehicleDAO);
        MaintenanceEntryService maintenanceEntryService = new MaintenanceEntryService(emf, maintenanceEntryDAO, mileageEntryDAO, vehicleDAO);
        MileageEntryService mileageEntryService = new MileageEntryService(emf, mileageEntryDAO, vehicleDAO);
        StatisticsService statisticsService = new StatisticsService(vehicleDAO, fuelEntryDAO, maintenanceEntryDAO, mileageEntryDAO);

        // === SECURITY ===
        this.jwtMiddleware = new JWTMiddleware(jwtService);

        // === HANDLERS ===
        VehicleHandler vehicleHandler = new VehicleHandler(vehicleService);
        FuelEntryHandler fuelEntryHandler = new FuelEntryHandler(fuelEntryService);
        MaintenanceEntryHandler maintenanceEntryHandler = new MaintenanceEntryHandler(maintenanceEntryService);
        MileageEntryHandler mileageEntryHandler = new MileageEntryHandler(mileageEntryService);
        StatisticsHandler statisticsHandler = new StatisticsHandler(statisticsService);
        UserHandler userHandler = new UserHandler(userService, jwtService);

        // === CONTROLLERS ===
        this.userController = new UserController(userHandler);
        this.vehicleController = new VehicleController(vehicleHandler);
        this.fuelEntryController = new FuelEntryController(fuelEntryHandler);
        this.maintenanceEntryController = new MaintenanceEntryController(maintenanceEntryHandler);
        this.mileageEntryController = new MileageEntryController(mileageEntryHandler);
        this.statisticsController = new StatisticsController(statisticsHandler);
    }

    @Override
    public void addEndpoints() {
        userController.addEndpoints();
        vehicleController.addEndpoints();
        fuelEntryController.addEndpoints();
        maintenanceEntryController.addEndpoints();
        mileageEntryController.addEndpoints();
        statisticsController.addEndpoints();
    }

    public Javalin start(int port) {
        Javalin app = Javalin.create(config -> {
            config.showJavalinBanner = false;
            config.jsonMapper(new JavalinJackson().updateMapper(mapper -> mapper.registerModule(new JavaTimeModule())));
            config.router.apiBuilder(this);
        });

        app.before("/api/v1/vehicles", jwtMiddleware.authenticate());
        app.before("/api/v1/vehicles/*", jwtMiddleware.authenticate());

        setupExceptions(app);

        app.start(port);

        return app;
    }

    public void stop(Javalin app) {
        app.stop();
    }

    private static void setupExceptions(Javalin app) {
        app.exception(ApiException.class, (e, ctx) -> ctx.status(e.getStatus()).json(Map.of("message", e.getMessage())));

        app.exception(BadRequestException.class, (e, ctx) -> ctx.status(HttpStatus.BAD_REQUEST).json(Map.of("message", e.getMessage())));

        app.exception(ConflictException.class, (e, ctx) -> ctx.status(HttpStatus.CONFLICT).json(Map.of("message", e.getMessage())));

        app.exception(DatabaseException.class, (e, ctx) -> ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(Map.of("message", "A database error occurred")));

        app.exception(EntityNotFoundException.class, (e, ctx) -> ctx.status(HttpStatus.NOT_FOUND).json(Map.of("message", e.getMessage())));

        app.exception(MotorAPIException.class, (e, ctx) -> ctx.status(502).json(Map.of("message", "Vehicle lookup service is temporarily unavailable")));

        app.exception(Exception.class, (e, ctx) -> ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(Map.of("message", "An unexpected error occurred")));
    }
}
