package com.kevin.spring.uber.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.*;

/* Spring Data - <Location, id> */
public interface LocationRepository extends JpaRepository<Location, Long> {
    /* DAO about Location Domain Class */
    /* Page class for pagination */
    Page<Location> findByVehicleMovementType(@Param("movementType") Location.VehicleMovementType vehicleMovementType, Pageable pageable);
    Page<Location> findByUnitInfoUnitVin(@Param("unitVin") String unitVin, Pageable pageable);
}
