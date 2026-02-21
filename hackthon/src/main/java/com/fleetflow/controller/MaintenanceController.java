package com.fleetflow.controller;

import com.fleetflow.dto.maintenance.CreateMaintenanceRequest;
import com.fleetflow.dto.maintenance.MaintenanceResponse;
import com.fleetflow.service.MaintenanceLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/maintenance")
@RequiredArgsConstructor
public class MaintenanceController {

    private final MaintenanceLogService maintenanceService;

    @PreAuthorize("hasAuthority('MAINTENANCE_CREATE')")
    @PostMapping
    public ResponseEntity<MaintenanceResponse> create(
            @Valid @RequestBody CreateMaintenanceRequest request){
        return ResponseEntity.ok(
                maintenanceService.create(request)
        );
    }

    @PreAuthorize("hasAuthority('MAINTENANCE_UPDATE')")
    @PutMapping("/{id}/complete")
    public ResponseEntity<MaintenanceResponse> complete(
            @PathVariable Long id){
        return ResponseEntity.ok(
                maintenanceService.complete(id)
        );
    }

    @PreAuthorize("hasAuthority('MAINTENANCE_READ')")
    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<List<MaintenanceResponse>> getByVehicle(
            @PathVariable Long vehicleId){
        return ResponseEntity.ok(
                maintenanceService.getByVehicle(vehicleId)
        );
    }
}