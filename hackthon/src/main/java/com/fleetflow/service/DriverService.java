package com.fleetflow.service;

import com.fleetflow.dto.driver.CreateDriverRequest;
import com.fleetflow.dto.driver.DriverResponse;
import com.fleetflow.dto.driver.UpdateDriverRequest;
import com.fleetflow.enums.DriverStatus;

import java.util.List;

public interface DriverService {
    DriverResponse create(CreateDriverRequest request);
    DriverResponse getById(Long id);
    List<DriverResponse> getAll(DriverStatus status);
    DriverResponse update(Long id, UpdateDriverRequest request);
}
