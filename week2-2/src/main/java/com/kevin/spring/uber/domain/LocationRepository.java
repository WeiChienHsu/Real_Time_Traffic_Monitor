package com.kevin.spring.uber.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.*;

/* Spring Data - <Location, id> */
/* Repository scanning mechanism: Spring is instructed to scan repositories
* and all its subpackages for interfaces extending Repository or one of
* its sub interfaces.
*
* For each interface found, the infrastructure registers the persistence
* technology-specific FactoryBean to create the appropriate proxies that
* handle invocations of the query methods.
* */

public interface LocationRepository extends JpaRepository<Location, Long> {
    /* DAO about Location Domain Class - No need to read Queries */
    /* Page class for pagination */
    Page<Location> findByVehicleMovementType(@Param("movementType") Location.VehicleMovementType vehicleMovementType, Pageable pageable);
    Page<Location> findByUnitInfoUnitVin(@Param("unitVin") String unitVin, Pageable pageable);
}
