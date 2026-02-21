package com.fleetflow.service;

import com.fleetflow.dto.analytics.DriverPerformanceResponse;
import com.fleetflow.dto.analytics.VehicleOperationalCostResponse;
import com.fleetflow.dto.analytics.VehicleROIResponse;

public interface AnalyticsService {
    Double getVehicleProfit(Long vehicleId);
    Double getVehicleFuelEfficiency(Long vehicleId);
    Double getVehicleCostPerKm(Long vehicleId);
    VehicleROIResponse getVehicleROI(Long vehicleId);
    VehicleOperationalCostResponse getVehicleOperationalCost(Long vehicleId);
    DriverPerformanceResponse getDriverCompletionRate(Long driverId);
}
