package com.fleetflow.dto.maintenance;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CreateMaintenanceRequest {

    @NotNull
    private Long vehicleId;

    @NotBlank
    private String description;

    @NotNull
    @Positive
    private Double cost;

    private LocalDate serviceDate;
}