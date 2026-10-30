package app.tests.utils;

import app.entities.User;
import app.entities.Vehicle;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.time.LocalDate;
import java.util.UUID;

public class TestDataFactory {
    public static Long createTestVehicle(EntityManagerFactory emf, User user) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            User managedUser = em.find(User.class, user.getUserId());

            if (managedUser == null) {
                throw new IllegalArgumentException("Test user does not exist");
            }

            LocalDate now = LocalDate.now();

            Vehicle vehicle = Vehicle.builder()
                    .registrationNumber("TEST-" + UUID.randomUUID())
                    .make("Audi")
                    .model("A6")
                    .modelYear(2015)
                    .user(managedUser)
                    .createdAt(now)
                    .updatedAt(now)
                    .build();

            em.persist(vehicle);
            em.getTransaction().commit();

            return vehicle.getVehicleId();
        }
    }
}
