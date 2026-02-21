package com.fleetflow.repository;

import com.fleetflow.entity.FuelLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface FuelLogRepository
        extends JpaRepository<FuelLog, Long> {

    List<FuelLog> findByVehicleId(Long vehicleId);

    List<FuelLog> findByDateBetween(LocalDate start, LocalDate end);

    @Query("""
       SELECT COALESCE(SUM(f.cost),0)
       FROM FuelLog f
       WHERE f.vehicle.id = :vehicleId
       """)
    Double getTotalFuelCost(Long vehicleId);

    @Query("""
       SELECT COALESCE(SUM(f.liters),0)
       FROM FuelLog f
       WHERE f.vehicle.id = :vehicleId
       """)
    Double getTotalLiters(Long vehicleId);
}