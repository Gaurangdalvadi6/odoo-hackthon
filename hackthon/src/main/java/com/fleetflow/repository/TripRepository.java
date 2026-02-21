package com.fleetflow.repository;

import com.fleetflow.entity.Trip;
import com.fleetflow.enums.TripStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TripRepository
        extends JpaRepository<Trip, Long> {

    List<Trip> findByVehicleId(Long vehicleId);

    List<Trip> findByDriverId(Long driverId);

    List<Trip> findByStatus(TripStatus status);
}
