package com.kevin.spring.uber.service.impl;

import com.kevin.spring.uber.domain.Location;
import com.kevin.spring.uber.domain.LocationRepository;
import com.kevin.spring.uber.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service /* Marked this Bean as a Service Layer */
public class LocationServiceImpl implements LocationService {

    private LocationRepository locationRepository;

    @Autowired
    public LocationServiceImpl(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;

    }

    @Override
    public List<Location> saveCarLocations(List<Location> carLocations) {
        /* Save method is extend from JPA Repository */
        return locationRepository.save(carLocations);

    }

    @Override
    public void deleteAll() {
        this.locationRepository.deleteAll();
    }

    @Override
    public Page<Location> findByVehicleMovementType(String movementType, Pageable pageable) {
        /* Avoid directly show the implementation instead of letting API user to pass a specific type */
        /* The job of transferring String into VehicleMovementType should be placed in Domain layer */
        return this.locationRepository.findByVehicleMovementType(Location.VehicleMovementType.valueOf(movementType), pageable);
    }

    @Override
    public Page<Location> findByVin(String vin, Pageable pageable) {
        return this.locationRepository.findByUnitInfoUnitVin(vin, pageable);
    }
}
