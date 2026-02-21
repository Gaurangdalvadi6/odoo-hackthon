package com.fleetflow.controller;

import com.fleetflow.dto.analytics.DriverPerformanceResponse;
import com.fleetflow.dto.analytics.VehicleOperationalCostResponse;
import com.fleetflow.dto.analytics.VehicleROIResponse;
import com.fleetflow.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vehicle/{id}/profit")
    public ResponseEntity<Double> getProfit(@PathVariable Long id){
        return ResponseEntity.ok(
                analyticsService.getVehicleProfit(id)
        );
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vehicle/{id}/cost-per-km")
    public ResponseEntity<Double> getCostPerKm(@PathVariable Long id){
        return ResponseEntity.ok(
                analyticsService.getVehicleCostPerKm(id)
        );
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vehicle/{id}/fuel-efficiency")
    public ResponseEntity<Double> getFuelEfficiency(@PathVariable Long id){
        return ResponseEntity.ok(
                analyticsService.getVehicleFuelEfficiency(id)
        );
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vehicle/{id}/roi")
    public ResponseEntity<VehicleROIResponse> getVehicleROI(@PathVariable Long id){
        return ResponseEntity.ok(
                analyticsService.getVehicleROI(id)
        );
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vehicle/{id}/operational-cost")
    public ResponseEntity<VehicleOperationalCostResponse> getOperationalCost(@PathVariable Long id){
        return ResponseEntity.ok(
                analyticsService.getVehicleOperationalCost(id)
        );
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/driver/{id}/completion-rate")
    public ResponseEntity<DriverPerformanceResponse> getDriverCompletionRate(@PathVariable Long id){
        return ResponseEntity.ok(
                analyticsService.getDriverCompletionRate(id)
        );
    }
}
