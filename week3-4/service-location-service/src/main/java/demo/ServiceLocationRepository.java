package demo;

import org.springframework.data.geo.Point;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.Description;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

/**
 *
 *
 */
@RepositoryRestResource(collectionResourceRel = "locations")
public interface ServiceLocationRepository
        extends PagingAndSortingRepository<ServiceLocation, Long> {

    @RestResource(rel = "by-location", description = @Description("Find by location, comma separated, e.g. 'lat,long', and distance, e.g. '50km'") )
    ServiceLocation findFirstByLocationNear(@Param("location") Point location);

}
