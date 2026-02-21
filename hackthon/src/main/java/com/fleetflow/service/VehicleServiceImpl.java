package com.fleetflow.service;

import com.fleetflow.dto.vehicle.CreateVehicleRequest;
import com.fleetflow.dto.vehicle.VehicleResponse;
import com.fleetflow.entity.Vehicle;
import com.fleetflow.enums.VehicleStatus;
import com.fleetflow.repository.VehicleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepo;

    @Override
    public VehicleResponse create(CreateVehicleRequest request) {

        Vehicle vehicle = Vehicle.builder()
                .model(request.getModel())
                .licensePlate(request.getLicensePlate())
                .maxCapacity(request.getMaxCapacity())
                .status(VehicleStatus.AVAILABLE)
                .acquisitionCost(request.getAcquisitionCost())
                .build();

        vehicleRepo.save(vehicle);

        return map(vehicle);
    }

    @Override
    public List<VehicleResponse> getAll() {
        return vehicleRepo.findAll()
                .stream()
                .map(this::map)
                .toList();
    }

    @Override
    public void retireVehicle(Long id) {
        Vehicle vehicle = vehicleRepo.findById(id)
                .orElseThrow();
        vehicle.setStatus(VehicleStatus.RETIRED);
    }

    private VehicleResponse map(Vehicle v){
        return VehicleResponse.builder()
                .id(v.getId())
                .model(v.getModel())
                .licensePlate(v.getLicensePlate())
                .status(v.getStatus())
                .build();
    }
}
