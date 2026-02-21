package com.fleetflow.service;

import com.fleetflow.dto.trip.CreateTripRequest;
import com.fleetflow.dto.trip.TripResponse;
import com.fleetflow.enums.TripStatus;

import java.util.List;

public interface TripService {
    TripResponse create(CreateTripRequest request);
    TripResponse dispatch(Long id);
    TripResponse complete(Long id, Double finalOdometer);
    TripResponse cancel(Long id);
    List<TripResponse> getAll(TripStatus status);
    TripResponse getById(Long id);
}