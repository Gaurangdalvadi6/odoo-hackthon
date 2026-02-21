package com.fleetflow.service;

import com.fleetflow.dto.vehicle.CreateVehicleRequest;
import com.fleetflow.dto.vehicle.UpdateVehicleRequest;
import com.fleetflow.dto.vehicle.VehicleResponse;
import com.fleetflow.enums.VehicleStatus;
import com.fleetflow.enums.VehicleType;

import java.util.List;

public interface VehicleService {

    VehicleResponse create(CreateVehicleRequest request);

    VehicleResponse getById(Long id);

    List<VehicleResponse> getAll(VehicleType type, VehicleStatus status, String region);

    VehicleResponse update(Long id, UpdateVehicleRequest request);

    void retireVehicle(Long id);
}
