package com.fleetflow.dto.analytics;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DriverPerformanceResponse {

    private Long driverId;
    private String driverName;
    private long totalTrips;
    private long completedTrips;
    private double completionRate;
}
