package com.fleetflow.repository;

import com.fleetflow.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VehicleAnalyticsRepository
        extends JpaRepository<Vehicle, Long> {

    @Query("""
           SELECT COALESCE(SUM(f.cost),0)
           FROM FuelLog f
           WHERE f.vehicle.id = :vehicleId
           """)
    Double getTotalFuelCost(Long vehicleId);

    @Query("""
       SELECT COALESCE(SUM(m.cost),0)
       FROM MaintenanceLog m
       WHERE m.vehicle.id = :vehicleId
       """)
    Double getTotalMaintenanceCost(Long vehicleId);

    @Query("""
       SELECT COALESCE(SUM(t.distance),0) / NULLIF(COALESCE(SUM(f.liters),0),0)
       FROM Trip t
       JOIN FuelLog f ON t.vehicle.id = f.vehicle.id
       WHERE t.vehicle.id = :vehicleId
       """)
    Double calculateFuelEfficiency(Long vehicleId);

    @Query("""
        SELECT ((COALESCE(SUM(t.revenue),0) -(COALESCE(SUM(f.cost),0) + COALESCE(SUM(m.cost),0)))/ v.acquisitionCost)
        FROM Vehicle v
        LEFT JOIN Trip t ON t.vehicle.id = v.id AND t.status='COMPLETED'
        LEFT JOIN FuelLog f ON f.vehicle.id = v.id
        LEFT JOIN MaintenanceLog m ON m.vehicle.id = v.id
        WHERE v.id = :vehicleId
        """)
    Double calculateVehicleROI(Long vehicleId);

    @Query("""
        SELECT MONTH(f.date),SUM(f.cost)
        FROM FuelLog f
        WHERE YEAR(f.date) = :year
        GROUP BY MONTH(f.date)
        ORDER BY MONTH(f.date)
        """)
    List<Object[]> getMonthlyFuelExpense(int year);
}