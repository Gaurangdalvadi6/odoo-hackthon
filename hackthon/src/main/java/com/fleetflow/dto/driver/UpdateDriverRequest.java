package com.fleetflow.dto.driver;

import com.fleetflow.enums.DriverStatus;
import com.fleetflow.enums.VehicleType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UpdateDriverRequest {

    private String name;
    private String licenseNumber;
    private LocalDate licenseExpiry;
    private VehicleType licenseCategory;
    private DriverStatus status;
    private Double safetyScore;
}
