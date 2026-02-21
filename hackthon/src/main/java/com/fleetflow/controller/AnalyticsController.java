package com.fleetflow.controller;

import com.fleetflow.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/vehicle/{id}/profit")
    public ResponseEntity<Double> getProfit(@PathVariable Long id){
        return ResponseEntity.ok(
                analyticsService.getVehicleProfit(id)
        );
    }
}
