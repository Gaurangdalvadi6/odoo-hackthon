package com.fleetflow.dto.analytics;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class VehicleOperationalCostResponse {

    private Long vehicleId;
    private String vehiclePlate;
    private Double totalFuelCost;
    private Double totalMaintenanceCost;
    private Double totalOperationalCost;  // fuel + maintenance
}
