package com.fleetflow.dto.dashboard;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DashboardResponse {

    private long activeFleet;        // Vehicles "On Trip"
    private long maintenanceAlerts; // Vehicles "In Shop"
    private double utilizationRate;  // % assigned (On Trip) vs total active (Available + On Trip + In Shop)
    private long pendingCargo;       // Trips in DRAFT status (shipments waiting for assignment)
}
