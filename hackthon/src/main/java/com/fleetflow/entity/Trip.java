package com.fleetflow.entity;

import com.fleetflow.enums.TripStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "trips")
@Getter
@Setter
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;

    private Double cargoWeight;

    private Double revenue;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private Double startOdometer;
    private Double endOdometer;

    private Double distance;

    @Enumerated(EnumType.STRING)
    private TripStatus status;
    // DRAFT, DISPATCHED, COMPLETED, CANCELLED
}