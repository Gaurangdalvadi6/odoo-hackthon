package com.fleetflow.service;

import com.fleetflow.repository.FuelLogRepository;
import com.fleetflow.repository.MaintenanceLogRepository;
import com.fleetflow.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final TripRepository tripRepo;
    private final FuelLogRepository fuelRepo;
    private final MaintenanceLogRepository maintenanceRepo;

    @Override
    public Double getVehicleProfit(Long vehicleId){

        Double revenue = tripRepo.getTotalRevenue(vehicleId);
        Double fuel = fuelRepo.getTotalFuelCost(vehicleId);
        Double maintenance = maintenanceRepo.getTotalMaintenanceCost(vehicleId);

        return revenue - (fuel + maintenance);
    }
}