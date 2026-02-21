package com.fleetflow.entity;

import com.fleetflow.enums.VehicleStatus;
import com.fleetflow.enums.VehicleType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "vehicles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String model;

    @Enumerated(EnumType.STRING)
    private VehicleType type;

    private String region;

    @Column(nullable = false, unique = true)
    private String licensePlate;

    @Column(nullable = false)
    private Double maxCapacity;

    private Double odometer;

    @Enumerated(EnumType.STRING)
    private VehicleStatus status;
    // AVAILABLE, ON_TRIP, IN_SHOP, RETIRED

    private Double acquisitionCost;

    private Double totalFuelCost;

    @CreationTimestamp
    private LocalDateTime createdAt;
}