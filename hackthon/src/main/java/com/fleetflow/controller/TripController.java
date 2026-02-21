package com.fleetflow.controller;

import com.fleetflow.dto.trip.CreateTripRequest;
import com.fleetflow.dto.trip.TripResponse;
import com.fleetflow.enums.TripStatus;
import com.fleetflow.service.TripService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trips")
@RequiredArgsConstructor
public class TripController {

    private final TripService tripService;

    @PreAuthorize("hasAuthority('TRIP_CREATE')")
    @PostMapping
    public ResponseEntity<TripResponse> create(
            @Valid @RequestBody CreateTripRequest request){
        return ResponseEntity.ok(tripService.create(request));
    }

    @PreAuthorize("hasAuthority('TRIP_CREATE')")
    @PutMapping("/{id}/dispatch")
    public ResponseEntity<TripResponse> dispatch(@PathVariable Long id){
        return ResponseEntity.ok(tripService.dispatch(id));
    }

    @PreAuthorize("hasAuthority('TRIP_UPDATE')")
    @PutMapping("/{id}/complete")
    public ResponseEntity<TripResponse> complete(
            @PathVariable Long id,
            @RequestParam Double finalOdometer){
        return ResponseEntity.ok(
                tripService.complete(id, finalOdometer)
        );
    }

    @PreAuthorize("hasAuthority('TRIP_UPDATE')")
    @PutMapping("/{id}/cancel")
    public ResponseEntity<TripResponse> cancel(@PathVariable Long id){
        return ResponseEntity.ok(tripService.cancel(id));
    }

    @PreAuthorize("hasAuthority('TRIP_READ')")
    @GetMapping("/{id}")
    public ResponseEntity<TripResponse> getById(@PathVariable Long id){
        return ResponseEntity.ok(tripService.getById(id));
    }

    @PreAuthorize("hasAuthority('TRIP_READ')")
    @GetMapping
    public ResponseEntity<List<TripResponse>> getAll(
            @RequestParam(required = false) TripStatus status){
        return ResponseEntity.ok(tripService.getAll(status));
    }
}