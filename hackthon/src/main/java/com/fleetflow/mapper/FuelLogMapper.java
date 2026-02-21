package com.fleetflow.mapper;

import com.fleetflow.dto.fuellog.FuelLogResponse;
import com.fleetflow.entity.FuelLog;
import org.springframework.stereotype.Component;

@Component
public class FuelLogMapper {

    public FuelLogResponse toResponse(FuelLog fuelLog){
        return FuelLogResponse.builder()
                .id(fuelLog.getId())
                .vehiclePlate(fuelLog.getVehicle().getLicensePlate())
                .liters(fuelLog.getLiters())
                .cost(fuelLog.getCost())
                .date(fuelLog.getDate())
                .build();
    }
}