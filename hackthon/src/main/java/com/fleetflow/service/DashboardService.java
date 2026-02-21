package com.fleetflow.service;

import com.fleetflow.dto.dashboard.DashboardResponse;
import com.fleetflow.enums.TripStatus;
import com.fleetflow.enums.VehicleStatus;
import com.fleetflow.repository.TripRepository;
import com.fleetflow.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final VehicleRepository vehicleRepo;
    private final TripRepository tripRepo;

    @Transactional(readOnly = true)
    public DashboardResponse getDashboard() {
        long onTrip = vehicleRepo.countByStatus(VehicleStatus.ON_TRIP);
        long inShop = vehicleRepo.countByStatus(VehicleStatus.IN_SHOP);
        long available = vehicleRepo.countByStatus(VehicleStatus.AVAILABLE);
        long pendingCargo = tripRepo.countByStatus(TripStatus.DRAFT);

        long totalActive = onTrip + inShop + available;
        double utilizationRate = totalActive > 0 ? (onTrip * 100.0 / totalActive) : 0;

        return DashboardResponse.builder()
                .activeFleet(onTrip)
                .maintenanceAlerts(inShop)
                .utilizationRate(Math.round(utilizationRate * 100.0) / 100.0)
                .pendingCargo(pendingCargo)
                .build();
    }
}
