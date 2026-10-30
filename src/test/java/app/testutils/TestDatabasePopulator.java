package app.testutils;

import app.entities.*;
import app.enums.MaintenanceCategory;
import app.enums.MileageEntrySource;
import app.utils.PasswordEncrypter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceException;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

public class TestDatabasePopulator {
    public static Map<String, User> createUsers(EntityManagerFactory emf) {
        try (EntityManager em = emf.createEntityManager()) {
            PasswordEncrypter passwordEncrypter = new PasswordEncrypter();

            em.getTransaction().begin();

            User user1 = User.builder().username("John").email("JohnBakke@gmail.com").phone("40408080").password(passwordEncrypter.hashPassword("!johnbakke1")).build();
            User user2 = User.builder().username("Peter").email("PeterMand@gmail.com").phone("12345678").password(passwordEncrypter.hashPassword("!peterkode10")).build();
            User user3 = User.builder().username("Ida").email("IdaPigen@gmail.com").phone("21222324").password(passwordEncrypter.hashPassword("!idaskode123")).build();

            try {
                em.createNativeQuery("TRUNCATE TABLE users RESTART IDENTITY CASCADE").executeUpdate();
                em.persist(user1);
                em.persist(user2);
                em.persist(user3);

                em.flush();

            } catch (PersistenceException e) {
                if (em.getTransaction().isActive()) em.getTransaction().rollback();
                throw e;
            }
            em.getTransaction().commit();

            Map<String, User> users = new LinkedHashMap<>();
            users.put("user1", user1);
            users.put("user2", user2);
            users.put("user3", user3);

            return users;
        }
    }

    public static Map<String, Vehicle> createVehicles(EntityManagerFactory emf, Map<String, User> users) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            User user1 = em.find(User.class, users.get("user1").getUserId());
            User user2 = em.find(User.class, users.get("user2").getUserId());
            User user3 = em.find(User.class, users.get("user3").getUserId());

            Vehicle vehicle1 = Vehicle.builder().registrationNumber("CL65485").status("Registered").vin("JYARH12345A000001").type("Personbil").make("AUDI").model("A6").variant("3.0 TDI 272 HK QUATTRO").chassisType("Sedan").modelYear(2015).color("Sort").fuelType("Diesel").engineVolume(2967.0).enginePower(272.0).mileage(185000.0f).user(user1).build();
            Vehicle vehicle2 = Vehicle.builder().registrationNumber("AB12345").status("Registered").vin("JYARH12345A000002").type("Motorcycle").make("Yamaha").model("YZF-R3").variant("R3 Sport").chassisType("Sport").modelYear(2022).color("Blue").fuelType("Petrol").engineVolume(321.0).enginePower(42.0).mileage(12000.0f).user(user2).build();
            Vehicle vehicle3 = Vehicle.builder().registrationNumber("XY98765").status("Registered").vin("WVWZZZ1JZXW000003").type("Personbil").make("Volkswagen").model("Golf").variant("1.5 TSI").chassisType("Hatchback").modelYear(2020).color("White").fuelType("Petrol").engineVolume(1498.0).enginePower(150.0).mileage(65000.0f).user(user3).build();

            try {
                em.createNativeQuery("TRUNCATE TABLE vehicle RESTART IDENTITY CASCADE").executeUpdate();
                em.persist(vehicle1);
                em.persist(vehicle2);
                em.persist(vehicle3);

                em.flush();
            } catch (PersistenceException e) {
                if (em.getTransaction().isActive()) em.getTransaction().rollback();
                throw e;
            }
            em.getTransaction().commit();

            Map<String, Vehicle> vehicleMap = new LinkedHashMap<>();
            vehicleMap.put("vehicle1", vehicle1);
            vehicleMap.put("vehicle2", vehicle2);
            vehicleMap.put("vehicle3", vehicle3);

            return vehicleMap;
        }
    }

    public static void createEntries(EntityManagerFactory emf, Map<String, Vehicle> vehicles) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            try {
                Vehicle vehicle1 = em.find(Vehicle.class, vehicles.get("vehicle1").getVehicleId());
                Vehicle vehicle2 = em.find(Vehicle.class, vehicles.get("vehicle2").getVehicleId());
                Vehicle vehicle3 = em.find(Vehicle.class, vehicles.get("vehicle3").getVehicleId());

                createEntriesForVehicle(em, vehicle1);
                createEntriesForVehicle(em, vehicle2);
                createEntriesForVehicle(em, vehicle3);

                em.flush();
                em.getTransaction().commit();

            } catch (Exception e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                throw e;
            }
        }
    }

    private static void createEntriesForVehicle(EntityManager em, Vehicle vehicle) {
        LocalDate today = LocalDate.now();

        FuelEntry fuel1 = FuelEntry.builder()
                .date(today.minusDays(20))
                .mileage(vehicle.getMileage() - 1000)
                .liters(40.0)
                .price(600.0)
                .vehicle(vehicle)
                .build();

        MileageEntry mileage1 = MileageEntry.builder()
                .date(today.minusDays(20))
                .mileage(vehicle.getMileage() - 1000)
                .source(MileageEntrySource.MANUAL)
                .vehicle(vehicle)
                .fuelEntry(fuel1)
                .build();

        fuel1.setMileageEntry(mileage1);


        FuelEntry fuel2 = FuelEntry.builder()
                .date(today.minusDays(10))
                .mileage(vehicle.getMileage() - 500)
                .liters(35.0)
                .price(525.0)
                .vehicle(vehicle)
                .build();

        MileageEntry mileage2 = MileageEntry.builder()
                .date(today.minusDays(10))
                .mileage(vehicle.getMileage() - 500)
                .source(MileageEntrySource.MANUAL)
                .vehicle(vehicle)
                .fuelEntry(fuel2)
                .build();

        fuel2.setMileageEntry(mileage2);


        FuelEntry fuel3 = FuelEntry.builder()
                .date(today.minusDays(2))
                .mileage(vehicle.getMileage())
                .liters(30.0)
                .price(450.0)
                .vehicle(vehicle)
                .build();

        MileageEntry mileage3 = MileageEntry.builder()
                .date(today.minusDays(2))
                .mileage(vehicle.getMileage())
                .source(MileageEntrySource.MANUAL)
                .vehicle(vehicle)
                .fuelEntry(fuel3)
                .build();

        fuel3.setMileageEntry(mileage3);


        MaintenanceEntry maintenance = MaintenanceEntry.builder()
                .date(today.minusDays(5))
                .mileage(vehicle.getMileage() - 300)
                .category(MaintenanceCategory.OTHER)
                .description("Regular vehicle service")
                .cost(1200.0)
                .vehicle(vehicle)
                .build();

        MileageEntry maintenanceMileage = MileageEntry.builder()
                .date(today.minusDays(5))
                .mileage(vehicle.getMileage() - 300)
                .source(MileageEntrySource.MAINTENANCE_ENTRY)
                .vehicle(vehicle)
                .maintenanceEntry(maintenance)
                .build();

        maintenance.setMileageEntry(maintenanceMileage);

        em.persist(fuel1);
        em.persist(fuel2);
        em.persist(fuel3);
        em.persist(maintenance);

        em.persist(mileage1);
        em.persist(mileage2);
        em.persist(mileage3);
        em.persist(maintenanceMileage);
    }
}
