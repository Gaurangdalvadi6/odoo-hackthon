package com.fleetflow.controller;

import com.fleetflow.dto.fuellog.CreateFuelLogRequest;
import com.fleetflow.dto.fuellog.FuelLogResponse;
import com.fleetflow.service.FuelLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fuel-logs")
@RequiredArgsConstructor
public class FuelLogController {

    private final FuelLogService fuelLogService;

    @PreAuthorize("hasAuthority('FUEL_CREATE')")
    @PostMapping
    public ResponseEntity<FuelLogResponse> create(
            @Valid @RequestBody CreateFuelLogRequest request){
        return ResponseEntity.ok(fuelLogService.create(request));
    }

    @PreAuthorize("hasAuthority('FUEL_READ')")
    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<List<FuelLogResponse>> getByVehicle(
            @PathVariable Long vehicleId){
        return ResponseEntity.ok(
                fuelLogService.getByVehicle(vehicleId)
        );
    }
}