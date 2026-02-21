package com.fleetflow.service;

import com.fleetflow.dto.vehicle.CreateVehicleRequest;
import com.fleetflow.dto.vehicle.VehicleResponse;

import java.util.List;

public interface VehicleService {

    VehicleResponse create(CreateVehicleRequest request);

    List<VehicleResponse> getAll();

    void retireVehicle(Long id);
}
