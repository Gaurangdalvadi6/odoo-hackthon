package com.fleetflow.repository;

import com.fleetflow.entity.Trip;
import com.fleetflow.enums.TripStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TripRepository
        extends JpaRepository<Trip, Long> {

    List<Trip> findByVehicleId(Long vehicleId);

    List<Trip> findByDriverId(Long driverId);

    List<Trip> findByStatus(TripStatus status);

    @Query("""
       SELECT COALESCE(SUM(t.distance),0)
       FROM Trip t
       WHERE t.vehicle.id = :vehicleId
         AND t.status = 'COMPLETED'
       """)
    Double getTotalDistance(Long vehicleId);

    @Query("""
       SELECT COALESCE(SUM(t.revenue),0)
       FROM Trip t
       WHERE t.vehicle.id = :vehicleId
         AND t.status = 'COMPLETED'
       """)
    Double getTotalRevenue(Long vehicleId);
}
