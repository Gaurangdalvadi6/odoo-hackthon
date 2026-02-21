package com.fleetflow.repository;

import com.fleetflow.entity.MaintenanceLog;
import com.fleetflow.enums.MaintenanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MaintenanceLogRepository
        extends JpaRepository<MaintenanceLog, Long> {

    List<MaintenanceLog> findByVehicleId(Long vehicleId);

    List<MaintenanceLog> findByStatus(MaintenanceStatus status);
}
