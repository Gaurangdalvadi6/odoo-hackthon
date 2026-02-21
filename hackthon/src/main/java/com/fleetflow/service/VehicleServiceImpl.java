package com.fleetflow.service;

import com.fleetflow.dto.vehicle.CreateVehicleRequest;
import com.fleetflow.dto.vehicle.UpdateVehicleRequest;
import com.fleetflow.dto.vehicle.VehicleResponse;
import com.fleetflow.entity.Vehicle;
import com.fleetflow.enums.VehicleStatus;
import com.fleetflow.enums.VehicleType;
import com.fleetflow.exception.CustomException;
import com.fleetflow.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                .type(request.getType())
                .region(request.getRegion())
                .licensePlate(request.getLicensePlate())
                .maxCapacity(request.getMaxCapacity())
                .status(VehicleStatus.AVAILABLE)
                .acquisitionCost(request.getAcquisitionCost())
                .build();

        vehicleRepo.save(vehicle);

        return map(vehicle);
    }

    @Override
    @Transactional(readOnly = true)
    public VehicleResponse getById(Long id) {
        Vehicle vehicle = vehicleRepo.findById(id)
                .orElseThrow(() -> new CustomException("Vehicle not found", HttpStatus.NOT_FOUND));
        return map(vehicle);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehicleResponse> getAll(VehicleType type, VehicleStatus status, String region) {
        List<Vehicle> vehicles;
        if (type != null && status != null && region != null) {
            vehicles = vehicleRepo.findByTypeAndStatusAndRegion(type, status, region);
        } else if (type != null && status != null) {
            vehicles = vehicleRepo.findByTypeAndStatus(type, status);
        } else if (type != null && region != null) {
            vehicles = vehicleRepo.findByTypeAndRegion(type, region);
        } else if (status != null && region != null) {
            vehicles = vehicleRepo.findByStatusAndRegion(status, region);
        } else if (type != null) {
            vehicles = vehicleRepo.findByType(type);
        } else if (status != null) {
            vehicles = vehicleRepo.findByStatus(status);
        } else if (region != null) {
            vehicles = vehicleRepo.findByRegion(region);
        } else {
            vehicles = vehicleRepo.findAll();
        }
        return vehicles.stream().map(this::map).toList();
    }

    @Override
    public VehicleResponse update(Long id, UpdateVehicleRequest request) {
        Vehicle vehicle = vehicleRepo.findById(id)
                .orElseThrow(() -> new CustomException("Vehicle not found", HttpStatus.NOT_FOUND));
        if (request.getModel() != null) vehicle.setModel(request.getModel());
        if (request.getType() != null) vehicle.setType(request.getType());
        if (request.getRegion() != null) vehicle.setRegion(request.getRegion());
        if (request.getLicensePlate() != null) vehicle.setLicensePlate(request.getLicensePlate());
        if (request.getMaxCapacity() != null) vehicle.setMaxCapacity(request.getMaxCapacity());
        if (request.getOdometer() != null) vehicle.setOdometer(request.getOdometer());
        return map(vehicle);
    }

    @Override
    public void retireVehicle(Long id) {
        Vehicle vehicle = vehicleRepo.findById(id)
                .orElseThrow(() -> new CustomException("Vehicle not found", HttpStatus.NOT_FOUND));
        vehicle.setStatus(VehicleStatus.RETIRED);
    }

    private VehicleResponse map(Vehicle v){
        return VehicleResponse.builder()
                .id(v.getId())
                .model(v.getModel())
                .type(v.getType())
                .region(v.getRegion())
                .licensePlate(v.getLicensePlate())
                .maxCapacity(v.getMaxCapacity())
                .odometer(v.getOdometer())
                .status(v.getStatus())
                .acquisitionCost(v.getAcquisitionCost())
                .build();
    }
}
