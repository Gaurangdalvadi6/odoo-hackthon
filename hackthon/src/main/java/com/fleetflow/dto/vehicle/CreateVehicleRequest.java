package com.fleetflow.dto.vehicle;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateVehicleRequest {

    @NotBlank
    private String model;

    @NotBlank
    private String licensePlate;

    @NotNull
    @Positive
    private Double maxCapacity;

    private Double acquisitionCost;
}