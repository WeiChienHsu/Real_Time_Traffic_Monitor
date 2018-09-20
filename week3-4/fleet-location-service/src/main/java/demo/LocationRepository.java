package demo;

import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RestResource;

/* Spring Data REST - directly expose REST end point */
public interface LocationRepository extends PagingAndSortingRepository<Location, Long> {

    @RestResource(rel = "by-service-type")
    Page<Location> findByServiceType(@Param("type") String type, Pageable pageable);

    /* localhost:8080/locations/search/vin?vin=ba07dd85-4380-4c4b-9461-6b88564fee2c */
    @RestResource(path = "vin", rel = "by-vin")
    Page<Location> findByUnitInfoUnitVin(@Param("vin") String vin, Pageable pageable);

}
