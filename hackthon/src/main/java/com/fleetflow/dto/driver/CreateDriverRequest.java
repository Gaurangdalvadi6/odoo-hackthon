package com.fleetflow.dto.driver;

import com.fleetflow.enums.VehicleType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CreateDriverRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String licenseNumber;

    @Future
    private LocalDate licenseExpiry;

    private VehicleType licenseCategory;
}