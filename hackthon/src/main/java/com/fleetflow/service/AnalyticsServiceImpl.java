package com.fleetflow.service;

import com.fleetflow.dto.analytics.DriverPerformanceResponse;
import com.fleetflow.dto.analytics.VehicleOperationalCostResponse;
import com.fleetflow.dto.analytics.VehicleROIResponse;
import com.fleetflow.entity.Driver;
import com.fleetflow.entity.Vehicle;
import com.fleetflow.enums.TripStatus;
import com.fleetflow.exception.CustomException;
import com.fleetflow.repository.DriverRepository;
import com.fleetflow.repository.FuelLogRepository;
import com.fleetflow.repository.MaintenanceLogRepository;
import com.fleetflow.repository.TripRepository;
import com.fleetflow.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final TripRepository tripRepo;
    private final FuelLogRepository fuelRepo;
    private final MaintenanceLogRepository maintenanceRepo;
    private final VehicleRepository vehicleRepo;
    private final DriverRepository driverRepo;

    @Override
    public Double getVehicleProfit(Long vehicleId){

        Double revenue = tripRepo.getTotalRevenue(vehicleId);
        Double fuel = fuelRepo.getTotalFuelCost(vehicleId);
        Double maintenance = maintenanceRepo.getTotalMaintenanceCost(vehicleId);

        return (revenue != null ? revenue : 0) - ((fuel != null ? fuel : 0) + (maintenance != null ? maintenance : 0));
    }

    @Override
    public Double getVehicleFuelEfficiency(Long vehicleId) {
        Double totalDistance = tripRepo.getTotalDistance(vehicleId);
        Double totalLiters = fuelRepo.getTotalLiters(vehicleId);
        if (totalLiters == null || totalLiters == 0) return 0.0;
        return (totalDistance != null ? totalDistance : 0) / totalLiters;
    }

    @Override
    public Double getVehicleCostPerKm(Long vehicleId) {
        Double totalFuelCost = fuelRepo.getTotalFuelCost(vehicleId);
        Double totalDistance = tripRepo.getTotalDistance(vehicleId);
        if (totalDistance == null || totalDistance == 0) return 0.0;
        return (totalFuelCost != null ? totalFuelCost : 0) / totalDistance;
    }

    @Override
    public VehicleROIResponse getVehicleROI(Long vehicleId) {
        Vehicle vehicle = vehicleRepo.findById(vehicleId)
                .orElseThrow(() -> new CustomException("Vehicle not found", HttpStatus.NOT_FOUND));

        Double revenue = tripRepo.getTotalRevenue(vehicleId);
        Double fuel = fuelRepo.getTotalFuelCost(vehicleId);
        Double maintenance = maintenanceRepo.getTotalMaintenanceCost(vehicleId);
        Double acquisitionCost = vehicle.getAcquisitionCost();

        double rev = revenue != null ? revenue : 0;
        double totCost = (fuel != null ? fuel : 0) + (maintenance != null ? maintenance : 0);
        double acqCost = acquisitionCost != null && acquisitionCost > 0 ? acquisitionCost : 1;

        double roi = (rev - totCost) / acqCost;

        return VehicleROIResponse.builder()
                .vehicleId(vehicleId)
                .revenue(rev)
                .totalCost(totCost)
                .acquisitionCost(acquisitionCost != null ? acquisitionCost : 0)
                .roi(Math.round(roi * 10000.0) / 10000.0)
                .build();
    }

    @Override
    public VehicleOperationalCostResponse getVehicleOperationalCost(Long vehicleId) {
        Vehicle vehicle = vehicleRepo.findById(vehicleId)
                .orElseThrow(() -> new CustomException("Vehicle not found", HttpStatus.NOT_FOUND));

        Double fuel = fuelRepo.getTotalFuelCost(vehicleId);
        Double maintenance = maintenanceRepo.getTotalMaintenanceCost(vehicleId);

        double totalFuel = fuel != null ? fuel : 0;
        double totalMaint = maintenance != null ? maintenance : 0;

        return VehicleOperationalCostResponse.builder()
                .vehicleId(vehicleId)
                .vehiclePlate(vehicle.getLicensePlate())
                .totalFuelCost(totalFuel)
                .totalMaintenanceCost(totalMaint)
                .totalOperationalCost(totalFuel + totalMaint)
                .build();
    }

    @Override
    public DriverPerformanceResponse getDriverCompletionRate(Long driverId) {
        Driver driver = driverRepo.findById(driverId)
                .orElseThrow(() -> new CustomException("Driver not found", HttpStatus.NOT_FOUND));

        long totalTrips = tripRepo.countByDriverId(driverId);
        long completedTrips = tripRepo.countByDriverIdAndStatus(driverId, TripStatus.COMPLETED);
        double completionRate = totalTrips > 0 ? (completedTrips * 100.0 / totalTrips) : 0;

        return DriverPerformanceResponse.builder()
                .driverId(driverId)
                .driverName(driver.getName())
                .totalTrips(totalTrips)
                .completedTrips(completedTrips)
                .completionRate(Math.round(completionRate * 100.0) / 100.0)
                .build();
    }
}