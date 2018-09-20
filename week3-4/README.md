
## Fleet-Location-Service


## Fleet-Location-Simulator

### Model

- Define the model of data we need to use.

```java
/**
 * Input JSON file and deserialize to a Point
 */
public class GpsSimulatorRequest {

    private String vin;
    private Double speedInKph;
    private boolean move = true;
    private boolean exportPositionsToKml = false;
    private boolean exportPositionsToMessaging = true;
    private Integer reportInterval = 500;
    private int secondsToError = 0;
    private VehicleStatus vehicleStatus = VehicleStatus.NONE;
    private String polyline;
    private FaultCode faultCode;

    public Double getSpeedInKph() {
        return speedInKph;
    }

    public void setSpeedInKph(Double speedInKph) {
        this.speedInKph = speedInKph;
    }

    public boolean isMove() {
        return move;
    }

/* ..... */

    @Override
    public String toString() {
        return "GpsSimulatorRequest [vin=" + vin + ", speedInKph=" + speedInKph + ", move=" + move + ", exportPositionsToKml="
                + exportPositionsToKml + ", exportPositionsToMessaging=" + exportPositionsToMessaging
                + ", reportInterval=" + reportInterval + "]";
    }

    public void setFaultCode(FaultCode faultCode) {
        this.faultCode = faultCode;
    }

    public FaultCode getFaultCode() {
        return faultCode;
    }

}
```

### LocationSimulatorRESTApi

- Call "SimulatorFixture" in the model to get those data.
- Send to GpsSimulatorFactory to prepare creating the Simulator
- AsyncTaskExecutor: deal with async, after calling this API, we could do another tasks.

```java
/* Inject the Beans that we need to use */
@Autowired
private PathService pathService;

@Autowired
private GpsSimulatorFactory gpsSimulatorFactory;

@Autowired
private AsyncTaskExecutor taskExecutor;
```

### Service

- GpsSimulatorFactor: prepare creating the GpsSimulator

```java
@Service
public class DefaultGpsSimulatorFactory implements GpsSimulatorFactory {

    @Autowired
    private PathService pathService;

    @Autowired
    private PositionService positionService;

    private final AtomicLong instanceCounter = new AtomicLong();

    @Override
    public GpsSimulator prepareGpsSimulator(GpsSimulatorRequest gpsSimulatorRequest) {

        final GpsSimulator gpsSimulator = new GpsSimulator(gpsSimulatorRequest);

        gpsSimulator.setPositionInfoService(positionService);
        gpsSimulator.setId(this.instanceCounter.incrementAndGet());

        final List<Point> points = NavUtils.decodePolyline(gpsSimulatorRequest.getPolyline());
        gpsSimulator.setStartPoint(points.iterator().next());

        return prepareGpsSimulator(gpsSimulator, points);
    }

    @Override
    public GpsSimulator prepareGpsSimulator(GpsSimulator gpsSimulator, File kmlFile) {

        final List<Point> points;

        if (kmlFile == null) {
            points = this.pathService.getCoordinatesFromGoogle(this.pathService.loadDirectionInput().get(0));
        } else {
//            points = this.pathService.getCoordinatesFromKmlFile(kmlFile);
            points = new ArrayList<>();
        }

        return prepareGpsSimulator(gpsSimulator, points);
    }

    @Override
    public GpsSimulator prepareGpsSimulator(GpsSimulator gpsSimulator, List<Point> points) {
        gpsSimulator.setCurrentPosition(null);

        final List<Leg> legs = createLegsList(points);
        gpsSimulator.setLegs(legs);
        gpsSimulator.setStartPosition();
        return gpsSimulator;
    }

    /**
     * Creates list of legs in the path
     *
     * @param points
     */
    private List<Leg> createLegsList(List<Point> points) {
        final List<Leg> legs = new ArrayList<Leg>();
        for (int i = 0; i < (points.size() - 1); i++) {
            Leg leg = new Leg();
            leg.setId(i);
            leg.setStartPosition(points.get(i));
            leg.setEndPosition(points.get(i + 1));
            Double length = NavUtils.getDistance(points.get(i), points.get(i + 1));
            leg.setLength(length);
            Double heading = NavUtils.getBearing(points.get(i), points.get(i + 1));
            leg.setHeading(heading);
            legs.add(leg);
        }
        return legs;
    }
}
```

### Task

- GpsSimulator and GpsSimulatorInstance to store the data we get. 

```java
public class GpsSimulatorInstance {

    private long instanceId;
    private GpsSimulator gpsSimulator;
    private Future<?> gpsSimulatorTask;

    public GpsSimulatorInstance(long instanceId, GpsSimulator gpsSimulator, Future<?> gpsSimulatorTask) {
        super();
        this.instanceId = instanceId;
        this.gpsSimulator = gpsSimulator;
        this.gpsSimulatorTask = gpsSimulatorTask;
    }

    public long getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(long instanceId) {
        this.instanceId = instanceId;
    }

    public GpsSimulator getGpsSimulator() {
        return gpsSimulator;
    }

    public void setGpsSimulator(GpsSimulator gpsSimulator) {
        this.gpsSimulator = gpsSimulator;
    }

    public Future<?> getGpsSimulatorTask() {
        return gpsSimulatorTask;
    }

    public void setGpsSimulatorTask(Future<?> gpsSimulatorTask) {
        this.gpsSimulatorTask = gpsSimulatorTask;
    }

    @Override
    public String toString() {
        return "GpsSimulatorInstance [instanceId=" + instanceId + ", gpsSimulator=" + gpsSimulator
                + ", gpsSimulatorTask=" + gpsSimulatorTask + "]";
    }

}
```


## Fleet-Location-Ingest

- Binding the sources and inject MessageChannel created by springframework.cloud.
- Send the String of position information built by MessageBuilder.

```java
import org.springframework.cloud.stream.messaging.Source;

@EnableBinding(Source.class)
@RestController
public class VehiclePositionsSource {

    @Autowired
    private MessageChannel output;

    @RequestMapping(path = "/api/locations", method = RequestMethod.POST)
    public void locations(@RequestBody String positionInfo) {
        this.output.send(MessageBuilder.withPayload(positionInfo).build());
    }

}
```

- Configue the port and channel.

```yml
server:
  port: 9006
spring:
  application:
    name: fleet-location-ingest
  cloud:
    stream:
      bindings:
        output: vehicles
```