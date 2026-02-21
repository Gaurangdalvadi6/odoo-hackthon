package com.fleetflow.dto.fuellog;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class FuelLogResponse {

    private Long id;
    private String vehiclePlate;
    private Double liters;
    private Double cost;
    private LocalDate date;
}
