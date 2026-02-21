package com.fleetflow.repository;

import com.fleetflow.entity.MaintenanceLog;
import com.fleetflow.enums.MaintenanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MaintenanceLogRepository
        extends JpaRepository<MaintenanceLog, Long> {

    List<MaintenanceLog> findByVehicleId(Long vehicleId);

    List<MaintenanceLog> findByStatus(MaintenanceStatus status);

    @Query("""
       SELECT COALESCE(SUM(m.cost),0)
       FROM MaintenanceLog m
       WHERE m.vehicle.id = :vehicleId
       """)
    Double getTotalMaintenanceCost(Long vehicleId);
}
