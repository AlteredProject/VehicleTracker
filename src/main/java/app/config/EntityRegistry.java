package app.config;

import app.entities.*;
import org.hibernate.cfg.Configuration;

final class EntityRegistry {

    private EntityRegistry() {}

    static void registerEntities(Configuration configuration) {
        configuration.addAnnotatedClass(User.class);
        configuration.addAnnotatedClass(Vehicle.class);
        configuration.addAnnotatedClass(FuelEntry.class);
        configuration.addAnnotatedClass(MaintenanceEntry.class);
        configuration.addAnnotatedClass(MileageEntry.class);
    }
}