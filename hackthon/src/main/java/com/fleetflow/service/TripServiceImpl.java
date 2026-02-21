package com.fleetflow.service;

import com.fleetflow.dto.trip.CreateTripRequest;
import com.fleetflow.dto.trip.TripResponse;
import com.fleetflow.entity.Driver;
import com.fleetflow.entity.Trip;
import com.fleetflow.entity.Vehicle;
import com.fleetflow.enums.DriverStatus;
import com.fleetflow.enums.TripStatus;
import com.fleetflow.enums.VehicleStatus;
import com.fleetflow.exception.CustomException;
import com.fleetflow.repository.DriverRepository;
import com.fleetflow.repository.TripRepository;
import com.fleetflow.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TripServiceImpl implements TripService {

    private final TripRepository tripRepo;
    private final VehicleRepository vehicleRepo;
    private final DriverRepository driverRepo;

    @Override
    public TripResponse create(CreateTripRequest request) {

        Vehicle vehicle = vehicleRepo.findById(request.getVehicleId())
                .orElseThrow(() -> new CustomException("Vehicle not found", HttpStatus.NOT_FOUND));

        Driver driver = driverRepo.findById(request.getDriverId())
                .orElseThrow(() -> new CustomException("Driver not found", HttpStatus.NOT_FOUND));

        if(vehicle.getStatus() != VehicleStatus.AVAILABLE)
            throw new CustomException("Vehicle not available", HttpStatus.BAD_REQUEST);

        if(driver.getLicenseExpiry().isBefore(LocalDate.now()))
            throw new CustomException("License expired",HttpStatus.BAD_REQUEST);
        if(driver.getStatus() == DriverStatus.SUSPENDED)
            throw new CustomException("Driver is suspended", HttpStatus.BAD_REQUEST);
        if (driver.getLicenseCategory() != null && vehicle.getType() != null
                && driver.getLicenseCategory() != vehicle.getType())
            throw new CustomException("Driver license category does not match vehicle type", HttpStatus.BAD_REQUEST);

        if(request.getCargoWeight() > vehicle.getMaxCapacity())
            throw new CustomException("Capacity exceeded",HttpStatus.BAD_REQUEST);

        Trip trip = new Trip();
        trip.setVehicle(vehicle);
        trip.setDriver(driver);
        trip.setCargoWeight(request.getCargoWeight());
        trip.setOrigin(request.getOrigin());
        trip.setDestination(request.getDestination());
        trip.setRevenue(request.getRevenue());
        trip.setStatus(TripStatus.DRAFT);
        trip.setStartOdometer(vehicle.getOdometer() != null ? vehicle.getOdometer() : 0.0);

        tripRepo.save(trip);

        return map(trip);
    }

    @Override
    public TripResponse dispatch(Long id) {
        Trip trip = tripRepo.findById(id)
                .orElseThrow(() -> new CustomException("Trip not found", HttpStatus.NOT_FOUND));
        if (trip.getStatus() != TripStatus.DRAFT) {
            throw new CustomException("Only DRAFT trips can be dispatched", HttpStatus.BAD_REQUEST);
        }
        Vehicle vehicle = trip.getVehicle();
        Driver driver = trip.getDriver();
        if (vehicle.getStatus() != VehicleStatus.AVAILABLE) {
            throw new CustomException("Vehicle no longer available", HttpStatus.BAD_REQUEST);
        }
        if (driver.getLicenseExpiry().isBefore(LocalDate.now())) {
            throw new CustomException("Driver license expired", HttpStatus.BAD_REQUEST);
        }
        if (driver.getStatus() == DriverStatus.SUSPENDED) {
            throw new CustomException("Driver is suspended", HttpStatus.BAD_REQUEST);
        }
        if (driver.getLicenseCategory() != null && vehicle.getType() != null
                && driver.getLicenseCategory() != vehicle.getType()) {
            throw new CustomException("Driver license category does not match vehicle type", HttpStatus.BAD_REQUEST);
        }
        vehicle.setStatus(VehicleStatus.ON_TRIP);
        driver.setStatus(DriverStatus.ON_DUTY);
        trip.setStatus(TripStatus.DISPATCHED);
        trip.setStartTime(LocalDateTime.now());
        return map(trip);
    }

    @Override
    public TripResponse complete(Long id, Double finalOdometer) {

        Trip trip = tripRepo.findById(id)
                .orElseThrow(() -> new CustomException("Trip not found", HttpStatus.NOT_FOUND));
        if (trip.getStatus() != TripStatus.DISPATCHED) {
            throw new CustomException("Only DISPATCHED trips can be completed", HttpStatus.BAD_REQUEST);
        }

        trip.setStatus(TripStatus.COMPLETED);
        trip.setEndTime(LocalDateTime.now());
        trip.setEndOdometer(finalOdometer);

        Double startOdo = trip.getStartOdometer() != null ? trip.getStartOdometer() : 0.0;
        Double distance = finalOdometer - startOdo;
        if (distance < 0) {
            throw new CustomException("Invalid odometer reading: end must be >= start", HttpStatus.BAD_REQUEST);
        }
        trip.setDistance(distance);

        trip.getVehicle().setStatus(VehicleStatus.AVAILABLE);
        trip.getDriver().setStatus(DriverStatus.OFF_DUTY);

        trip.getVehicle().setOdometer(finalOdometer);

        return map(trip);
    }

    @Override
    public TripResponse cancel(Long id) {
        Trip trip = tripRepo.findById(id)
                .orElseThrow(() -> new CustomException("Trip not found", HttpStatus.NOT_FOUND));
        if (trip.getStatus() == TripStatus.COMPLETED || trip.getStatus() == TripStatus.CANCELLED) {
            throw new CustomException("Cannot cancel completed or already cancelled trip", HttpStatus.BAD_REQUEST);
        }
        if (trip.getStatus() == TripStatus.DISPATCHED) {
            trip.getVehicle().setStatus(VehicleStatus.AVAILABLE);
            trip.getDriver().setStatus(DriverStatus.OFF_DUTY);
        }
        trip.setStatus(TripStatus.CANCELLED);
        return map(trip);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TripResponse> getAll(TripStatus status) {
        var trips = status != null ? tripRepo.findByStatus(status) : tripRepo.findAll();
        return trips.stream().map(this::map).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TripResponse getById(Long id) {
        Trip trip = tripRepo.findById(id)
                .orElseThrow(() -> new CustomException("Trip not found", HttpStatus.NOT_FOUND));
        return map(trip);
    }

    private TripResponse map(Trip t){
        return TripResponse.builder()
                .id(t.getId())
                .vehiclePlate(t.getVehicle().getLicensePlate())
                .driverName(t.getDriver().getName())
                .cargoWeight(t.getCargoWeight())
                .origin(t.getOrigin())
                .destination(t.getDestination())
                .status(t.getStatus())
                .startTime(t.getStartTime())
                .endTime(t.getEndTime())
                .distance(t.getDistance())
                .build();
    }
}
