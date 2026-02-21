package com.fleetflow.repository;

import com.fleetflow.entity.Driver;
import com.fleetflow.enums.DriverStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface DriverRepository
        extends JpaRepository<Driver, Long> {

    List<Driver> findByStatus(DriverStatus status);

    List<Driver> findByLicenseExpiryBefore(LocalDate date);
}
