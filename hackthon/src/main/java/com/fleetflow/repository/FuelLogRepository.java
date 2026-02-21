package com.fleetflow.repository;

import com.fleetflow.entity.FuelLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface FuelLogRepository
        extends JpaRepository<FuelLog, Long> {

    List<FuelLog> findByVehicleId(Long vehicleId);

    List<FuelLog> findByDateBetween(LocalDate start, LocalDate end);
}