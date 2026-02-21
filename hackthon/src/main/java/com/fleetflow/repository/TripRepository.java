package com.fleetflow.repository;

import com.fleetflow.entity.Trip;
import com.fleetflow.enums.TripStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TripRepository
        extends JpaRepository<Trip, Long> {

    List<Trip> findByVehicleId(Long vehicleId);

    List<Trip> findByDriverId(Long driverId);

    List<Trip> findByStatus(TripStatus status);

    long countByStatus(TripStatus status);

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

    long countByDriverId(Long driverId);

    long countByDriverIdAndStatus(Long driverId, TripStatus status);

    @Query("""
       SELECT t FROM Trip t
       WHERE t.driver.id = :driverId
         AND t.status = 'COMPLETED'
         AND t.endTime >= :start AND t.endTime < :end
       """)
    List<Trip> findCompletedByDriverAndDateRange(@Param("driverId") Long driverId,
                                                 @Param("start") LocalDateTime start,
                                                 @Param("end") LocalDateTime end);
}
