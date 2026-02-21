package com.fleetflow.dto.maintenance;

import com.fleetflow.enums.MaintenanceStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class MaintenanceResponse {

    private Long id;
    private String vehiclePlate;
    private String description;
    private Double cost;
    private MaintenanceStatus status;
    private LocalDate serviceDate;
}
