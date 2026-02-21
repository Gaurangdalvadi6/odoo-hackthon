package com.fleetflow.service;

import com.fleetflow.dto.fuellog.CreateFuelLogRequest;
import com.fleetflow.dto.fuellog.FuelLogResponse;

import java.util.List;

public interface FuelLogService {

    FuelLogResponse create(CreateFuelLogRequest request);

    List<FuelLogResponse> getByVehicle(Long vehicleId);

    Double getTotalFuelCost(Long vehicleId);

    Double calculateFuelEfficiency(Long vehicleId);
}