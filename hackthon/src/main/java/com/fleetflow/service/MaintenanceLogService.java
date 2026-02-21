package com.fleetflow.service;

import com.fleetflow.dto.maintenance.CreateMaintenanceRequest;
import com.fleetflow.dto.maintenance.MaintenanceResponse;

import java.util.List;

public interface MaintenanceLogService {

    MaintenanceResponse create(CreateMaintenanceRequest request);

    MaintenanceResponse complete(Long maintenanceId);

    List<MaintenanceResponse> getByVehicle(Long vehicleId);

    Double getTotalMaintenanceCost(Long vehicleId);
}
