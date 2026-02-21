package com.fleetflow.dto.trip;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateTripRequest {

    @NotNull
    private Long vehicleId;

    @NotNull
    private Long driverId;

    @NotNull
    @Positive
    private Double cargoWeight;

    private Double revenue;
}