package com.fleetflow.service;

import com.fleetflow.dto.driver.CreateDriverRequest;
import com.fleetflow.dto.driver.DriverResponse;
import com.fleetflow.entity.Driver;
import com.fleetflow.enums.DriverStatus;
import com.fleetflow.repository.DriverRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepo;

    @Override
    public DriverResponse create(CreateDriverRequest request) {

        if(request.getLicenseExpiry().isBefore(LocalDate.now())){
            throw new RuntimeException("License already expired");
        }

        Driver driver = new Driver();
        driver.setName(request.getName());
        driver.setLicenseNumber(request.getLicenseNumber());
        driver.setLicenseExpiry(request.getLicenseExpiry());
        driver.setStatus(DriverStatus.OFF_DUTY);

        driverRepo.save(driver);

        return DriverResponse.builder()
                .id(driver.getId())
                .name(driver.getName())
                .status(driver.getStatus())
                .build();
    }
}