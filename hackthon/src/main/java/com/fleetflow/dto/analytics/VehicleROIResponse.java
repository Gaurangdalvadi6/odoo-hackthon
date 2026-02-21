package com.fleetflow.dto.analytics;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class VehicleROIResponse {

    private Long vehicleId;
    private Double revenue;
    private Double totalCost;       // fuel + maintenance
    private Double acquisitionCost;
    private Double roi;            // (Revenue - TotalCost) / AcquisitionCost
}
