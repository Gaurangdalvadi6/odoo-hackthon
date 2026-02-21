package com.fleetflow.service;

import com.fleetflow.dto.driver.CreateDriverRequest;
import com.fleetflow.dto.driver.DriverResponse;

public interface DriverService {
    DriverResponse create(CreateDriverRequest request);
}
