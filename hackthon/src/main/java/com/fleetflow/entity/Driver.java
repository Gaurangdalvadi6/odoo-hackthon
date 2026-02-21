package com.fleetflow.entity;

import com.fleetflow.enums.DriverStatus;
import com.fleetflow.enums.VehicleType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "drivers")
@Getter
@Setter
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String licenseNumber;

    private LocalDate licenseExpiry;

    @Enumerated(EnumType.STRING)
    private VehicleType licenseCategory;

    @Enumerated(EnumType.STRING)
    private DriverStatus status;
    // ON_DUTY, OFF_DUTY, SUSPENDED

    private Double safetyScore;
}