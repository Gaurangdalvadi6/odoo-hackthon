package com.fleetflow.dto.trip;

import com.fleetflow.enums.TripStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class TripResponse {

    private Long id;
    private String vehiclePlate;
    private String driverName;
    private Double cargoWeight;
    private TripStatus status;
    private LocalDateTime startTime;
}
