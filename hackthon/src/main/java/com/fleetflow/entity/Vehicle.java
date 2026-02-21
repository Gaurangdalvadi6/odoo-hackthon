package com.fleetflow.entity;

import com.fleetflow.enums.VehicleStatus;
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

    @Column(nullable = false, unique = true)
    private String licensePlate;

    @Column(nullable = false)
    private Double maxCapacity;

    private Double odometer;

    @Enumerated(EnumType.STRING)
    private VehicleStatus status;
    // AVAILABLE, ON_TRIP, IN_SHOP, RETIRED

    private Double acquisitionCost;

    @CreationTimestamp
    private LocalDateTime createdAt;
}