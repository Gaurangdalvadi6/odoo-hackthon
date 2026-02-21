package com.fleetflow.dto.vehicle;

import com.fleetflow.enums.VehicleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateVehicleRequest {

    private String model;
    private VehicleType type;
    private String region;
    private String licensePlate;
    @Positive
    private Double maxCapacity;
    private Double odometer;
}
