package com.fleetflow.service;

import com.fleetflow.dto.fuellog.CreateFuelLogRequest;
import com.fleetflow.dto.fuellog.FuelLogResponse;
import com.fleetflow.entity.FuelLog;
import com.fleetflow.entity.Vehicle;
import com.fleetflow.enums.VehicleStatus;
import com.fleetflow.repository.FuelLogRepository;
import com.fleetflow.repository.TripRepository;
import com.fleetflow.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FuelLogServiceImpl implements FuelLogService {

    private final FuelLogRepository fuelLogRepository;
    private final VehicleRepository vehicleRepository;
    private final TripRepository tripRepository;

    @Override
    public FuelLogResponse create(CreateFuelLogRequest request) {

        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        // 🚨 Business Rule 1: Retired vehicle cannot refuel
        if(vehicle.getStatus() == VehicleStatus.RETIRED){
            throw new RuntimeException("Cannot add fuel to retired vehicle");
        }

        FuelLog fuelLog = new FuelLog();
        fuelLog.setVehicle(vehicle);
        fuelLog.setLiters(request.getLiters());
        fuelLog.setCost(request.getCost());
        fuelLog.setDate(
                request.getDate() != null ? request.getDate() : LocalDate.now()
        );

        fuelLogRepository.save(fuelLog);

        return mapToResponse(fuelLog);
    }

    @Override
    @Transactional
    public List<FuelLogResponse> getByVehicle(Long vehicleId) {

        return fuelLogRepository.findByVehicleId(vehicleId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Double getTotalFuelCost(Long vehicleId) {

        Double total = fuelLogRepository.getTotalFuelCost(vehicleId);

        return total != null ? total : 0.0;
    }

    @Override
    @Transactional(readOnly = true)
    public Double calculateFuelEfficiency(Long vehicleId) {

        Double totalDistance = tripRepository.getTotalDistance(vehicleId);
        Double totalFuel = fuelLogRepository.getTotalLiters(vehicleId);

        if(totalFuel == null || totalFuel == 0)
            return 0.0;

        return totalDistance / totalFuel;
    }

    private FuelLogResponse mapToResponse(FuelLog fuelLog){

        return FuelLogResponse.builder()
                .id(fuelLog.getId())
                .vehiclePlate(fuelLog.getVehicle().getLicensePlate())
                .liters(fuelLog.getLiters())
                .cost(fuelLog.getCost())
                .date(fuelLog.getDate())
                .build();
    }
}