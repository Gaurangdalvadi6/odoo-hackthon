package com.fleetflow.controller;

import com.fleetflow.dto.vehicle.CreateVehicleRequest;
import com.fleetflow.dto.vehicle.UpdateVehicleRequest;
import com.fleetflow.dto.vehicle.VehicleResponse;
import com.fleetflow.enums.VehicleStatus;
import com.fleetflow.enums.VehicleType;
import com.fleetflow.service.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @PreAuthorize("hasAuthority('VEHICLE_CREATE')")
    @PostMapping
    public ResponseEntity<VehicleResponse> create(
            @Valid @RequestBody CreateVehicleRequest request){
        return ResponseEntity.ok(vehicleService.create(request));
    }

    @PreAuthorize("hasAuthority('VEHICLE_READ')")
    @GetMapping("/{id}")
    public ResponseEntity<VehicleResponse> getById(@PathVariable Long id){
        return ResponseEntity.ok(vehicleService.getById(id));
    }

    @PreAuthorize("hasAuthority('VEHICLE_READ')")
    @GetMapping
    public ResponseEntity<List<VehicleResponse>> getAll(
            @RequestParam(required = false) VehicleType type,
            @RequestParam(required = false) VehicleStatus status,
            @RequestParam(required = false) String region){
        return ResponseEntity.ok(vehicleService.getAll(type, status, region));
    }

    @PreAuthorize("hasAuthority('VEHICLE_UPDATE')")
    @PutMapping("/{id}")
    public ResponseEntity<VehicleResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateVehicleRequest request){
        return ResponseEntity.ok(vehicleService.update(id, request));
    }

    @PreAuthorize("hasAuthority('VEHICLE_UPDATE')")
    @PutMapping("/{id}/retire")
    public ResponseEntity<Void> retire(@PathVariable Long id){
        vehicleService.retireVehicle(id);
        return ResponseEntity.ok().build();
    }
}