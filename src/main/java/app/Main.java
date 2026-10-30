package app;

import app.config.ApplicationConfig;
import app.config.HibernateConfig;
import app.daos.*;
import jakarta.persistence.EntityManagerFactory;

public class Main {
    static void main() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();

        ApplicationConfig applicationConfig = new ApplicationConfig(emf);
        applicationConfig.start(7070);
    }
}
