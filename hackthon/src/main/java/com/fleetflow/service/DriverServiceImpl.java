package com.fleetflow.service;

import com.fleetflow.dto.driver.CreateDriverRequest;
import com.fleetflow.dto.driver.DriverResponse;
import com.fleetflow.dto.driver.UpdateDriverRequest;
import com.fleetflow.entity.Driver;
import com.fleetflow.enums.DriverStatus;
import com.fleetflow.exception.CustomException;
import com.fleetflow.repository.DriverRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepo;

    @Override
    public DriverResponse create(CreateDriverRequest request) {

        if(request.getLicenseExpiry().isBefore(LocalDate.now())){
            throw new CustomException("License already expired", HttpStatus.NOT_ACCEPTABLE);
        }

        Driver driver = new Driver();
        driver.setName(request.getName());
        driver.setLicenseNumber(request.getLicenseNumber());
        driver.setLicenseExpiry(request.getLicenseExpiry());
        driver.setLicenseCategory(request.getLicenseCategory());
        driver.setStatus(DriverStatus.OFF_DUTY);

        driverRepo.save(driver);

        return map(driver);
    }

    @Override
    public DriverResponse getById(Long id) {
        Driver driver = driverRepo.findById(id)
                .orElseThrow(() -> new CustomException("Driver not found", HttpStatus.NOT_FOUND));
        return map(driver);
    }

    @Override
    public List<DriverResponse> getAll(DriverStatus status) {
        List<Driver> drivers = status != null ? driverRepo.findByStatus(status) : driverRepo.findAll();
        return drivers.stream().map(this::map).toList();
    }

    @Override
    public DriverResponse update(Long id, UpdateDriverRequest request) {
        Driver driver = driverRepo.findById(id)
                .orElseThrow(() -> new CustomException("Driver not found", HttpStatus.NOT_FOUND));
        if (request.getName() != null) driver.setName(request.getName());
        if (request.getLicenseNumber() != null) driver.setLicenseNumber(request.getLicenseNumber());
        if (request.getLicenseExpiry() != null) driver.setLicenseExpiry(request.getLicenseExpiry());
        if (request.getLicenseCategory() != null) driver.setLicenseCategory(request.getLicenseCategory());
        if (request.getStatus() != null) driver.setStatus(request.getStatus());
        if (request.getSafetyScore() != null) driver.setSafetyScore(request.getSafetyScore());
        return map(driver);
    }

    private DriverResponse map(Driver d) {
        return DriverResponse.builder()
                .id(d.getId())
                .name(d.getName())
                .licenseNumber(d.getLicenseNumber())
                .licenseExpiry(d.getLicenseExpiry())
                .licenseCategory(d.getLicenseCategory())
                .status(d.getStatus())
                .safetyScore(d.getSafetyScore())
                .build();
    }
}