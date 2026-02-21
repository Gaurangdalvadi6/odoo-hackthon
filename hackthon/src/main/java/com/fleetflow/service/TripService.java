package com.fleetflow.service;

import com.fleetflow.dto.trip.CreateTripRequest;
import com.fleetflow.dto.trip.TripResponse;

public interface TripService {
    TripResponse create(CreateTripRequest request);
    TripResponse complete(Long id, Double finalOdometer);
}