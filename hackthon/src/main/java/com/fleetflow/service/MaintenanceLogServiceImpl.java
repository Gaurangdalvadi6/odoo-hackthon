package com.fleetflow.service;

import com.fleetflow.dto.maintenance.CreateMaintenanceRequest;
import com.fleetflow.dto.maintenance.MaintenanceResponse;
import com.fleetflow.entity.MaintenanceLog;
import com.fleetflow.entity.Vehicle;
import com.fleetflow.enums.MaintenanceStatus;
import com.fleetflow.enums.VehicleStatus;
import com.fleetflow.exception.CustomException;
import com.fleetflow.repository.MaintenanceLogRepository;
import com.fleetflow.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MaintenanceLogServiceImpl implements MaintenanceLogService {

    private final MaintenanceLogRepository maintenanceRepository;
    private final VehicleRepository vehicleRepository;

    @Override
    public MaintenanceResponse create(CreateMaintenanceRequest request) {

        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new CustomException("Vehicle not found", HttpStatus.NOT_FOUND));

        // 🚨 Rule 1: Retired vehicle cannot go to maintenance
        if (vehicle.getStatus() == VehicleStatus.RETIRED) {
            throw new CustomException("Cannot maintain retired vehicle",HttpStatus.NOT_ACCEPTABLE);
        }

        // 🚨 Rule 2: If already in shop, prevent duplicate open maintenance
        if (vehicle.getStatus() == VehicleStatus.IN_SHOP) {
            throw new CustomException("Vehicle already in maintenance",HttpStatus.NOT_ACCEPTABLE);
        }

        // Change vehicle status
        vehicle.setStatus(VehicleStatus.IN_SHOP);

        MaintenanceLog log = MaintenanceLog.builder()
                .vehicle(vehicle)
                .description(request.getDescription())
                .cost(request.getCost())
                .serviceDate(
                        request.getServiceDate() != null
                                ? request.getServiceDate()
                                : LocalDate.now()
                )
                .status(MaintenanceStatus.OPEN)
                .build();

        maintenanceRepository.save(log);

        return mapToResponse(log);
    }

    @Override
    public MaintenanceResponse complete(Long maintenanceId) {

        MaintenanceLog log = maintenanceRepository.findById(maintenanceId)
                .orElseThrow(() -> new CustomException("Maintenance not found",HttpStatus.NOT_FOUND));

        if (log.getStatus() == MaintenanceStatus.COMPLETED) {
            throw new CustomException("Maintenance already completed",HttpStatus.BAD_REQUEST);
        }

        log.setStatus(MaintenanceStatus.COMPLETED);

        // 🚨 When maintenance completes → Vehicle becomes AVAILABLE
        Vehicle vehicle = log.getVehicle();
        vehicle.setStatus(VehicleStatus.AVAILABLE);

        return mapToResponse(log);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaintenanceResponse> getByVehicle(Long vehicleId) {

        return maintenanceRepository.findByVehicleId(vehicleId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Double getTotalMaintenanceCost(Long vehicleId) {

        Double total = maintenanceRepository.getTotalMaintenanceCost(vehicleId);

        return total != null ? total : 0.0;
    }

    private MaintenanceResponse mapToResponse(MaintenanceLog log) {

        return MaintenanceResponse.builder()
                .id(log.getId())
                .vehiclePlate(log.getVehicle().getLicensePlate())
                .description(log.getDescription())
                .cost(log.getCost())
                .status(log.getStatus())
                .serviceDate(log.getServiceDate())
                .build();
    }
}
