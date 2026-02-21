package com.fleetflow.dto.driver;

import com.fleetflow.enums.DriverStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DriverResponse {

    private Long id;
    private String name;
    private DriverStatus status;
    private Double safetyScore;
}