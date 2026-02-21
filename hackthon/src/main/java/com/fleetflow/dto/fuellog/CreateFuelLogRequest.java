package com.fleetflow.dto.fuellog;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CreateFuelLogRequest {

    @NotNull
    private Long vehicleId;

    @NotNull
    @Positive
    private Double liters;

    @NotNull
    @Positive
    private Double cost;

    private LocalDate date;
}