package demo.service;

import demo.model.DirectionInput;
import demo.model.Point;
import demo.model.ServiceLocation;
import demo.model.SimulatorFixture;

import java.util.List;

public interface PathService {

    /**
     * @return
     */
    List<DirectionInput> loadDirectionInput();

    SimulatorFixture loadSimulatorFixture();

    /**
     * @param directionInput
     * @return
     */
    List<Point> getCoordinatesFromGoogle(DirectionInput directionInput);

    String getCoordinatesFromGoogleAsPolyline(DirectionInput directionInput);


//    List<Point> getCoordinatesFromKmlFile(File kmlFile);

    List<ServiceLocation> getServiceStations();
}