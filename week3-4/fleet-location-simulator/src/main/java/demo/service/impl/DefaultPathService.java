package demo.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.maps.*;
import com.google.maps.model.*;
import demo.model.DirectionInput;
import demo.model.ServiceLocation;
import demo.model.SimulatorFixture;
import net.sf.sprockets.Sprockets;
import net.sf.sprockets.google.Place;
import net.sf.sprockets.google.Places;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 *
 */
@Service
public class DefaultPathService implements PathService {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private Environment environment;

//    @Autowired
//    private Unmarshaller unmarshaller;

    public DefaultPathService() {
        super();
    }

    /* (non-Javadoc)
     * @see frk.gpssimulator.service.impl.PathServiceInterface#loadDirections()
     */
    @Override
    public List<DirectionInput> loadDirectionInput() {
        final InputStream is = this.getClass().getResourceAsStream("/directions.json");

        try {
            return objectMapper.readValue(is, new TypeReference<List<DirectionInput>>() {
                //Just make Jackson happy
            });
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /* (non-Javadoc)
     * @see frk.gpssimulator.service.impl.PathServiceInterface#loadDirections()
     */
    @Override
    public SimulatorFixture loadSimulatorFixture() {
        final InputStream is = this.getClass().getResourceAsStream("/fixture.json");

        try {
            return objectMapper.readValue(is, SimulatorFixture.class);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String getCoordinatesFromGoogleAsPolyline(DirectionInput directionInput) {
        final GeoApiContext context = new GeoApiContext()
                .setApiKey(environment.getRequiredProperty("gpsSimmulator.googleApiKey"));
        final DirectionsApiRequest request = DirectionsApi.getDirections(
                context,
                directionInput.getFrom(),
                directionInput.getTo());

        try {
            DirectionsRoute[] routes = request.await();
            return routes[0].overviewPolyline.getEncodedPath();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public List<ServiceLocation> getServiceStations() {

        Sprockets.getConfig()
                .setProperty("google.api-key", environment.getRequiredProperty("gpsSimmulator.googleApiKey"));

        List<Place> stations = null;
        try {
            //White House location
            stations = Places.nearbySearch(new Params().location(38.8976763, -77.0365298).radius(5000)
                    .keyword("gasoline").openNow().maxResults(6000)).getResult();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        final List<ServiceLocation> serviceLocations = new ArrayList<>();
        final GeoApiContext context = new GeoApiContext()
                .setApiKey(environment.getRequiredProperty("gpsSimmulator.googleApiKey"));

        for (Place place : stations) {
            final ServiceLocation serviceLocation = new ServiceLocation(place.getLatitude(), place.getLongitude());
            final GeocodingApiRequest request = GeocodingApi
                    .reverseGeocode(context, new LatLng(place.getLatitude(), place.getLongitude()));

            try {
                final GeocodingResult[] result = request.await();

                String street = "";
                String streetNumber = "";

                for (AddressComponent addressComponent : result[0].addressComponents) {
                    for (AddressComponentType type : addressComponent.types) {
                        switch (type) {
                            case ROUTE:
                                street = addressComponent.shortName;
                                break;
                            case STREET_NUMBER:
                                streetNumber = addressComponent.shortName;
                                break;
                            case LOCALITY:
                                serviceLocation.setCity(addressComponent.longName);
                                break;
                            case ADMINISTRATIVE_AREA_LEVEL_1:
                                serviceLocation.setState(addressComponent.shortName);
                                break;
                            case POSTAL_CODE:
                                serviceLocation.setZip(addressComponent.shortName);
                                break;
                            default:
                                break;
                        }
                    }
                }
                serviceLocation.setAddress1(streetNumber + " " + street);
                serviceLocation.setType("Service");
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }

            serviceLocations.add(serviceLocation);
        }

        return serviceLocations;
    }

    /* (non-Javadoc)
     * @see frk.gpssimulator.service.impl.PathServiceInterface#getCoordinatesFromGoogle(frk.gpssimulator.model.DirectionInput)
     */
    @Override
    public List<Point> getCoordinatesFromGoogle(DirectionInput directionInput) {

        final GeoApiContext context = new GeoApiContext()
                .setApiKey(environment.getRequiredProperty("gpsSimmulator.googleApiKey"));
        final DirectionsApiRequest request = DirectionsApi.getDirections(
                context,
                directionInput.getFrom(),
                directionInput.getTo());
        List<LatLng> latlongList = null;

        try {
            DirectionsRoute[] routes = request.await();

            for (DirectionsRoute route : routes) {
                latlongList = route.overviewPolyline.decodePath();
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

        final List<Point> points = new ArrayList<>(latlongList.size());

        for (LatLng latLng : latlongList) {
            points.add(new Point(latLng.lat, latLng.lng));
        }

        return points;
    }

    /* (non-Javadoc)
     * @see frk.gpssimulator.service.KmlService#getCoordinates(java.io.File)
     */
//    @Override
//    public final List<Point> getCoordinatesFromKmlFile(File kmlFile) {
//
//        final Kml kml;
//        try {
//            kml = (Kml) unmarshaller.unmarshal(new StreamSource(kmlFile));
//        } catch (XmlMappingException | IOException e) {
//            throw new IllegalStateException(e);
//        }
//
//        final Document doc = (Document) kml.getFeature();
//        List<Feature> features = doc.getFeature();
//        List<Point> pointsToReturn = new ArrayList<Point>();
//
//        for (Feature feature : features) {
//            if (feature instanceof Placemark) {
//                final Placemark placemark = (Placemark) feature;
//                if (placemark.getGeometry() instanceof LineString) {
//                    final LineString lineString = (LineString) placemark.getGeometry();
//                    List<Coordinate> coordinates = lineString.getCoordinates();
//                    for (Coordinate coord : coordinates) {
//                        Point point2 = new Point(
//                                coord.getLatitude(),
//                                coord.getLongitude());
//                        pointsToReturn.add(point2);
//                    }
//                    break;
//                }
//            }
//
//        }
//        return pointsToReturn;
//
//    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void setGoogleApiKey(String googleApiKey) {
        Assert.hasText(googleApiKey, "The googleApiKey must not be empty.");

    }

}
