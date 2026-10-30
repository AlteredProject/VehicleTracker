package app.services;

import app.daos.*;
import app.dtos.statistics.*;
import app.entities.*;
import app.enums.StatisticsPeriod;
import app.exceptions.BadRequestException;
import app.exceptions.EntityNotFoundException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Stream;

public class StatisticsService {
    private static final double DAYS_PER_MONTH = 30.4375;

    private final VehicleDAO vehicleDAO;
    private final FuelEntryDAO fuelEntryDAO;
    private final MaintenanceEntryDAO maintenanceEntryDAO;
    private final MileageEntryDAO mileageEntryDAO;

    public StatisticsService(VehicleDAO vehicleDAO, FuelEntryDAO fuelEntryDAO, MaintenanceEntryDAO maintenanceEntryDAO, MileageEntryDAO mileageEntryDAO) {
        this.vehicleDAO = vehicleDAO;
        this.fuelEntryDAO = fuelEntryDAO;
        this.maintenanceEntryDAO = maintenanceEntryDAO;
        this.mileageEntryDAO = mileageEntryDAO;
    }

    public VehicleStatisticsDTO getStatistics(Long vehicleId, Integer userId, StatisticsPeriod period) {
        if (vehicleId == null || userId == null || period == null) {
            throw new BadRequestException("Required statistics parameters are missing");
        }

        Vehicle vehicle = vehicleDAO.getVehicleByIdAndUserId(vehicleId, userId);

        if (vehicle == null) {
            throw new EntityNotFoundException("Vehicle not found");
        }

        LocalDate endDate = LocalDate.now();
        LocalDate startDate = getStartDate(period, endDate);

        List<FuelEntry> fuelEntries = fuelEntryDAO.getByVehicleIdAndDateRange(vehicleId, startDate, endDate);

        List<MaintenanceEntry> maintenanceEntries = maintenanceEntryDAO.getByVehicleIdAndDateRange(vehicleId, startDate, endDate);

        List<MileageEntry> allMileageEntries = mileageEntryDAO.getAllByVehicleId(vehicleId);

        List<MileageEntry> mileageEntries = allMileageEntries.stream()
                .filter(entry -> !entry.getDate().isAfter(endDate))
                .filter(entry -> startDate == null || !entry.getDate().isBefore(startDate))
                .sorted(Comparator.comparing(MileageEntry::getDate))
                .toList();

        List<MileageEntry> mileageEntriesUpToToday = allMileageEntries.stream()
                .filter(entry -> !entry.getDate().isAfter(endDate))
                .sorted(Comparator.comparing(MileageEntry::getDate))
                .toList();

        Double distance = getDistanceOrNull(mileageEntriesUpToToday, mileageEntries, startDate);

        ExpenseStatisticsDTO expenses = calculateExpenses(fuelEntries, maintenanceEntries, distance, startDate, endDate);

        FuelStatisticsDTO fuel = calculateFuelStatistics(fuelEntries);

        MileageStatisticsDTO mileage = calculateMileageStatistics(mileageEntriesUpToToday, mileageEntries, startDate, endDate, distance);

        return new VehicleStatisticsDTO(period, startDate, endDate, expenses, fuel, mileage);
    }

    private LocalDate getStartDate(StatisticsPeriod period, LocalDate endDate) {
        return switch (period) {
            case WEEK -> endDate.minusWeeks(1);
            case MONTH -> endDate.minusMonths(1);
            case YEAR -> endDate.minusYears(1);
            case ALL -> null;
        };
    }


    // === EXPENSE STATISTICS ===
    private ExpenseStatisticsDTO calculateExpenses(List<FuelEntry> fuelEntries, List<MaintenanceEntry> maintenanceEntries, Double totalDistance, LocalDate startDate, LocalDate endDate) {
        double fuelExpenses = fuelEntries.stream()
                .mapToDouble(FuelEntry::getPrice)
                .sum();

        double maintenanceExpenses = maintenanceEntries.stream()
                .mapToDouble(MaintenanceEntry::getCost)
                .sum();

        double totalExpenses = fuelExpenses + maintenanceExpenses;

        Map<LocalDate, double[]> dailyExpenses = new TreeMap<>();

        for (FuelEntry entry : fuelEntries) {
            dailyExpenses.computeIfAbsent(entry.getDate(), d -> new double[2])[0] += entry.getPrice();
        }

        for (MaintenanceEntry entry : maintenanceEntries) {
            dailyExpenses.computeIfAbsent(entry.getDate(), d -> new double[2])[1] += entry.getCost();
        }

        List<ExpenseDataPointDTO> dataPoints = dailyExpenses.entrySet().stream()
                .map(entry -> new ExpenseDataPointDTO(entry.getKey(), entry.getValue()[0], entry.getValue()[1]))
                .toList();

        Double averageMonthlyExpenses = calculateAverageMonthlyExpenses(totalExpenses, fuelEntries, maintenanceEntries, startDate, endDate);

        Double fuelCostPerKm = calculateCostPerKm(fuelExpenses, totalDistance);
        Double maintenanceCostPerKm = calculateCostPerKm(maintenanceExpenses, totalDistance);
        Double totalCostPerKm = calculateCostPerKm(totalExpenses, totalDistance);

        return new ExpenseStatisticsDTO(totalExpenses, fuelExpenses, maintenanceExpenses, averageMonthlyExpenses, fuelCostPerKm, maintenanceCostPerKm, totalCostPerKm, dataPoints);
    }

    private double calculateAverageMonthlyExpenses(double totalExpenses, List<FuelEntry> fuelEntries, List<MaintenanceEntry> maintenanceEntries, LocalDate startDate, LocalDate endDate) {
        LocalDate effectiveStart = startDate;

        if (effectiveStart == null) {
            effectiveStart = getFirstExpenseDate(fuelEntries, maintenanceEntries);
        }

        if (effectiveStart == null) {
            return 0;
        }

        long days = Math.max(1, ChronoUnit.DAYS.between(effectiveStart, endDate) + 1);

        double months = days / DAYS_PER_MONTH;

        return totalExpenses / months;
    }

    private LocalDate getFirstExpenseDate(List<FuelEntry> fuelEntries, List<MaintenanceEntry> maintenanceEntries) {
        return Stream.concat(fuelEntries.stream().map(FuelEntry::getDate),
                maintenanceEntries.stream().map(MaintenanceEntry::getDate)).min(Comparator.naturalOrder()).orElse(null);
    }

    private Double calculateCostPerKm(double expenses, Double distance) {
        if (distance == null || distance <= 0) {
            return null;
        }
        return expenses / distance;
    }


    // === FUEL STATISTICS ===
    private FuelStatisticsDTO calculateFuelStatistics(List<FuelEntry> fuelEntries) {
        double totalLitres = fuelEntries.stream()
                .mapToDouble(FuelEntry::getLiters)
                .sum();

        double totalFuelCost = fuelEntries.stream()
                .mapToDouble(FuelEntry::getPrice)
                .sum();

        double averagePricePerLitre = totalLitres > 0 ? totalFuelCost / totalLitres : 0;

        double averageRefuellingCost = fuelEntries.isEmpty() ? 0 : totalFuelCost / fuelEntries.size();

        Double cheapestPrice = fuelEntries.stream()
                .filter(entry -> entry.getLiters() > 0)
                .mapToDouble(entry -> entry.getPrice() / entry.getLiters())
                .min()
                .stream()
                .boxed()
                .findFirst()
                .orElse(null);

        Double mostExpensivePrice = fuelEntries.stream()
                .filter(entry -> entry.getLiters() > 0)
                .mapToDouble(entry -> entry.getPrice() / entry.getLiters())
                .max()
                .stream()
                .boxed()
                .findFirst()
                .orElse(null);

        List<FuelEntry> sortedEntries = fuelEntries.stream()
                .sorted(Comparator.comparing(FuelEntry::getDate))
                .toList();

        List<FuelDataPointDTO> dataPoints = sortedEntries.stream()
                .map(entry -> new FuelDataPointDTO(entry.getDate(), entry.getLiters(), entry.getLiters() > 0 ? entry.getPrice() / entry.getLiters() : 0, entry.getPrice(), entry.getMileage()))
                .toList();

        Double averageConsumption = calculateAverageConsumption(sortedEntries);

        return new FuelStatisticsDTO(totalLitres, averagePricePerLitre, totalFuelCost, averageConsumption, averageRefuellingCost, cheapestPrice, mostExpensivePrice, dataPoints);
    }

    private Double calculateAverageConsumption(List<FuelEntry> entries) {
        if (entries.size() < 2) {
            return null;
        }

        double totalLitres = 0;
        double totalDistance = 0;

        for (int i = 1; i < entries.size(); i++) {
            FuelEntry previous = entries.get(i - 1);
            FuelEntry current = entries.get(i);

            double distance = current.getMileage() - previous.getMileage();

            if (distance <= 0) {
                continue;
            }

            totalDistance += distance;
            totalLitres += current.getLiters();
        }

        if (totalDistance <= 0) {
            return null;
        }
        return (totalLitres / totalDistance) * 100;
    }

    // === MILEAGE STATISTICS ===
    private MileageStatisticsDTO calculateMileageStatistics(List<MileageEntry> allMileageEntries, List<MileageEntry> periodEntries, LocalDate startDate, LocalDate endDate, Double totalDistance) {
        double currentMileage = allMileageEntries.isEmpty() ? 0 : allMileageEntries.getLast().getMileage();

        List<MileageDataPointDTO> dataPoints = periodEntries.stream()
                .map(entry -> new MileageDataPointDTO(entry.getDate(), entry.getMileage()))
                .toList();

        Double averageDailyDistance = null;
        Double averageMonthlyDistance = null;

        if (totalDistance != null) {
            LocalDate effectiveStart = startDate;

            if (effectiveStart == null && !allMileageEntries.isEmpty()) {
                effectiveStart = allMileageEntries.getFirst().getDate();
            }

            if (effectiveStart != null) {
                long days = Math.max(1, ChronoUnit.DAYS.between(effectiveStart, endDate));

                averageDailyDistance = totalDistance / days;
                averageMonthlyDistance = averageDailyDistance * DAYS_PER_MONTH;
            }
        }
        return new MileageStatisticsDTO(currentMileage, totalDistance, averageDailyDistance, averageMonthlyDistance, dataPoints);
    }

    private Double getDistanceOrNull(List<MileageEntry> allMileageEntries, List<MileageEntry> periodEntries, LocalDate startDate) {
        if (periodEntries.isEmpty()) {
            return null;
        }

        if (startDate == null) {
            if (allMileageEntries.size() < 2) {
                return null;
            }
            return (double) Math.max(0, allMileageEntries.getLast().getMileage() - allMileageEntries.getFirst().getMileage());
        }

        MileageEntry previousEntry = allMileageEntries.stream()
                .filter(entry -> entry.getDate().isBefore(startDate))
                .reduce((first, second) -> second)
                .orElse(null);

        if (previousEntry == null) {
            return null;
        }

        return (double) Math.max(0, periodEntries.getLast().getMileage() - previousEntry.getMileage());
    }
}