package com.fleetflow.repository;

import com.fleetflow.entity.Vehicle;
import com.fleetflow.enums.VehicleStatus;
import com.fleetflow.enums.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VehicleRepository
        extends JpaRepository<Vehicle, Long> {

    Optional<Vehicle> findByLicensePlate(String licensePlate);

    List<Vehicle> findByStatus(VehicleStatus status);

    List<Vehicle> findByType(VehicleType type);

    List<Vehicle> findByRegion(String region);

    List<Vehicle> findByTypeAndStatus(VehicleType type, VehicleStatus status);

    List<Vehicle> findByTypeAndRegion(VehicleType type, String region);

    List<Vehicle> findByStatusAndRegion(VehicleStatus status, String region);

    List<Vehicle> findByTypeAndStatusAndRegion(VehicleType type, VehicleStatus status, String region);

    long countByStatus(VehicleStatus status);

    long count();
}