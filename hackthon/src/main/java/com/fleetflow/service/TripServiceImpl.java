package com.fleetflow.service;

import com.fleetflow.dto.trip.CreateTripRequest;
import com.fleetflow.dto.trip.TripResponse;
import com.fleetflow.entity.Driver;
import com.fleetflow.entity.Trip;
import com.fleetflow.entity.Vehicle;
import com.fleetflow.enums.DriverStatus;
import com.fleetflow.enums.TripStatus;
import com.fleetflow.enums.VehicleStatus;
import com.fleetflow.repository.DriverRepository;
import com.fleetflow.repository.TripRepository;
import com.fleetflow.repository.VehicleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
                .orElseThrow();

        Driver driver = driverRepo.findById(request.getDriverId())
                .orElseThrow();

        if(vehicle.getStatus() != VehicleStatus.AVAILABLE)
            throw new RuntimeException("Vehicle not available");

        if(driver.getLicenseExpiry().isBefore(LocalDate.now()))
            throw new RuntimeException("License expired");

        if(request.getCargoWeight() > vehicle.getMaxCapacity())
            throw new RuntimeException("Capacity exceeded");

        vehicle.setStatus(VehicleStatus.ON_TRIP);
        driver.setStatus(DriverStatus.ON_DUTY);

        Trip trip = new Trip();
        trip.setVehicle(vehicle);
        trip.setDriver(driver);
        trip.setCargoWeight(request.getCargoWeight());
        trip.setRevenue(request.getRevenue());
        trip.setStatus(TripStatus.DISPATCHED);
        trip.setStartTime(LocalDateTime.now());

        tripRepo.save(trip);

        return map(trip);
    }

    @Override
    public TripResponse complete(Long id, Double finalOdometer) {

        Trip trip = tripRepo.findById(id).orElseThrow();

        trip.setStatus(TripStatus.COMPLETED);
        trip.setEndTime(LocalDateTime.now());

//        trip.setStartOdometer(trip.getVehicle().getOdometer());
//        trip.setStartTime(LocalDateTime.now());
//
//        trip.setEndOdometer(finalOdometer);
//
//        Double distance = finalOdometer - trip.getStartOdometer();
//
//        if(distance < 0){
//            throw new RuntimeException("Invalid odometer reading");
//        }
//        trip.setDistance(distance);

        trip.getVehicle().setStatus(VehicleStatus.AVAILABLE);
        trip.getDriver().setStatus(DriverStatus.OFF_DUTY);

        trip.getVehicle().setOdometer(finalOdometer);

        return map(trip);
    }

    private TripResponse map(Trip t){
        return TripResponse.builder()
                .id(t.getId())
                .vehiclePlate(t.getVehicle().getLicensePlate())
                .driverName(t.getDriver().getName())
                .status(t.getStatus())
                .build();
    }
}
