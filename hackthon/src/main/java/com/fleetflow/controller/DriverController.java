package com.fleetflow.controller;

import com.fleetflow.dto.driver.CreateDriverRequest;
import com.fleetflow.dto.driver.DriverResponse;
import com.fleetflow.dto.driver.UpdateDriverRequest;
import com.fleetflow.enums.DriverStatus;
import com.fleetflow.service.DriverService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService;

    @PreAuthorize("hasAuthority('DRIVER_CREATE')")
    @PostMapping
    public ResponseEntity<DriverResponse> create(
            @Valid @RequestBody CreateDriverRequest request){
        return ResponseEntity.ok(driverService.create(request));
    }

    @PreAuthorize("hasAuthority('DRIVER_READ')")
    @GetMapping("/{id}")
    public ResponseEntity<DriverResponse> getById(@PathVariable Long id){
        return ResponseEntity.ok(driverService.getById(id));
    }

    @PreAuthorize("hasAuthority('DRIVER_READ')")
    @GetMapping
    public ResponseEntity<List<DriverResponse>> getAll(
            @RequestParam(required = false) DriverStatus status){
        return ResponseEntity.ok(driverService.getAll(status));
    }

    @PreAuthorize("hasAuthority('DRIVER_UPDATE')")
    @PutMapping("/{id}")
    public ResponseEntity<DriverResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateDriverRequest request){
        return ResponseEntity.ok(driverService.update(id, request));
    }
}