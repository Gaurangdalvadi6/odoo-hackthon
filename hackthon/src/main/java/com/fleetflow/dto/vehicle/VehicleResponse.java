package com.fleetflow.dto.vehicle;

import com.fleetflow.enums.VehicleStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class VehicleResponse {

    private Long id;
    private String model;
    private String licensePlate;
    private Double maxCapacity;
    private Double odometer;
    private VehicleStatus status;
}