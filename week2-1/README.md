# Spring Boot

## Spring Boot Application

```java
@SpringBootApplication
public class FleetLocationServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(FleetLocationServiceApplication.class, args);
    }
}
```

## Configuaion of POM.xml

```xml
    <name>hello-world-demo</name>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>1.4.0.RELEASE</version>
        <relativePath/>
    </parent>

    <properties>
        <java.version>1.8</java.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>
```

***

## Java Main Class (Application) - No need for imitializer

- @SpringBootApplication

```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication /* Enable AutoConfiguration and other stuffs */
public class HelloWorldDemoApplication {

    /* Main method to tell the entrance */
    public static void main(String[] args) {
        /* Find the Application Class */
        SpringApplication.run(HelloWorldDemoApplication.class, args);
    }

}

```

## RestController Class

```java
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldRestController {

    @GetMapping("/")
    public String helloWorld() {
        return "Hello Spring Boot!";
    }
}
```

## Run Fat JAR

- Automatically deploy on Tomcat running on localhost:8080

```
java -jar hello-spring-boot-1.0.0.BUILT-SNAPSHOT.jar
```

***

# Fleet Location Service


## Domain - Location

- JPA: Entity (Location Class will be stayed forever)

```java
package com.kevin.spring.uber.domain;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

public class Location {

    enum GpsStatus {
        EXCELLENT, OK, UNRELIABLE, BAD, NOFIX, UNKNOWN;
    }

    public enum VehicleMovementType {
        STOPPED, IN_MOTION;

        public boolean isMoving() {
            return this != STOPPED;
        }
    }

    /* JPA using annotation to mark the tag in database */
    @Id
    @GeneratedValue
    private Long id;

    private double latitude;
    private double longitude;
    private String heading;
    private double gpsSpeed;
    private GpsStatus gpsStatus;
    private double odometer;
    private double totalEngineTime;
    private double totalIdleTime;
    private double totalFuelUsage;
    private String address;
    private Date timestamp = new Date();
    private String tspProvider;
    private VehicleMovementType vehicleMovementType = VehicleMovementType.STOPPED;
    private String serviceType;

}
```

## Domain - UnitFault

- @Embeddable: 表示此类可以被插入某个entity中
- @JsonInclude: for seriazlize and deserizlize

```java
    @Embedded
    @AttributeOverride(name = "engineMake", column =  @Column(name = "unit_engine_make"))
    private final UnitInfo unitInfo;
```


### 省略 Getter and Setter - lambok

直接生成 Code ， 在生成的 Class 中出現。

- Before

```java
/* Unit for each different Car - ex. vin number */
public class UnitInfo {

    /* Vin number could not be changed after built */
    /* Immutable field */
    private final String unitVin;
    private String engineMake;
    private String customerName;
    private String unitNumber;
    
}
```

- After using Lombok.Data

```java
@Data
@RequiredArgsConstructor /* For initialize UnitInfo */
@AllArgsConstructor
public class UnitInfo {
    /* Vin number could not be changed after built */
    /* Immutable field */
    private final String unitVin;
    private String engineMake;
    private String customerName;
    private String unitNumber;

    /* Private Constructor: Avoid to be called from outside of Object */
    private UnitInfo() {
        this.unitVin = "";
    }
}
```

## JPA - Embedding + Lambok @Data 可以讓 Location調用其Data

```java

    @Embedded
    @AttributeOverride(name = "engineMake", column =  @Column(name = "unit_engine_make"))
    /* Override the column name in the UniInfo Class */

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "fmi", column = @Column(name = "unit_fmi")),
            @AttributeOverride(name = "spn", column = @Column(name = "unit_spn"))
    })
    private UnitFault unitFault;

    @JsonCreator
    private Location(@JsonProperty("vin") String vin) {
        this.unitInfo = new UnitInfo(vin);
    }

    public String getVin() {
        return this.unitInfo == null ? null : this.unitInfo.getUnitVin();
        /* Get Method was created by Lambok */
    }
```

***

# Jpa Repository

## LocationRepository Interface 

- Implement a location Repository
- Extends from JpaRepository (For Spring Data JPA)
- DAO about Location Domain Class


### JpaRepository Interface

```java
public interface JpaRepository<T, ID extends Serializable> extends PagingAndSortingRepository<T, ID> {
    List<T> findAll();

    List<T> findAll(Sort var1);

    List<T> findAll(Iterable<ID> var1);

    <S extends T> List<S> save(Iterable<S> var1);

    void flush();

    <S extends T> S saveAndFlush(S var1);

    void deleteInBatch(Iterable<T> var1);

    void deleteAllInBatch();

    T getOne(ID var1);
}
```

### LocationRepository Setup

- Param: movementType 對應 filed 的名字，生成Query的時候會去找 VehicleMovementType 做匹配

```java
/* Spring Data - <Location, id> */
public interface LocationRepository extends JpaRepository<Location, Long> {
    /* DAO about Location Domain Class */
    /* Page class for pagination */
    Page<Location> findByVehicleMovementType(@Param("movementType") Location.VehicleMovementType vehicleMovementType, Pageable pageable);
    Page<Location> findByUnitInfoUnitVin(@Param("unitVin") String unitVin, Pageable pageable);
}
```

***

# Service

## Location Service

盡量做成 Interface ， 因為會有不同的 Implementation， Service 是用來定義具體的邏輯。

- 完成 CURD 的 Interfaces

```java
public interface LocationService {
    List<Location> saveCarLocations(List<Location> carLocations);

    void deleteAll();

    Page<Location> findByVehicleMovementType(String movementType, Pageable pageable);

    Page<Location> findByUnitInfoUnitVin(String vin, Pageable pageable);

}
```

## Location Service Impl (Inside impl package)

- Need a Dependency Injection. (把 LocationRepository 注入目前要實現的Class當中)
- Constructor Injection.

```java
private LocationRepository locationRepository;

@Autowired
public LocationServiceImpl(LocationRepository locationRepository) {
    this.locationRepository = locationRepository;

}
```

- Service 層要儘量屏蔽所有業務邏輯實現，將其放在Domain曾當中。
- 分成兩層的原因：因為Method的Implement可能含有複雜的調用，ex: Call Vehicle registration web service to check valid or not，所以把 LocationServiceImpl 當作實現，LocationService當作Interface。

```java
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
```

### Marked the LocationServiceIml as a Service

```java
@Service
public class LocationServiceImpl implements LocationService {
```

***

# Rest Service

```java
/* Explore the Domain Layer Outside */
@RestController
public class FleetBulkUploadRestController {

    private LocationService locationService;

    @Autowired
    public FleetBulkUploadRestController(LocationService locationService) {
        this.locationService = locationService;
    }

    @RequestMapping(value = "/fleet", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public void upload(@RequestBody List<Location> locations) {
        this.locationService.saveCarLocations(locations);
    }

    @RequestMapping(value = "/purge", method = RequestMethod.POST)
    public void purge() {
        this.locationService.deleteAll();
    }

    @RequestMapping(value = "/fleet/{movementType}", method = RequestMethod.GET)
    public Page<Location> findByMovementType(@PathVariable String movementType, @RequestParam(name = "page") int page, @RequestParam(name = "size") int size) {
        return this.locationService.findByVehicleMovementType(movementType, new PageRequest(page, size));
    }

    @RequestMapping(value = "/fleet/vin/{vin}", method = RequestMethod.GET)
    public Page<Location> findByVinNumber(@PathVariable String vin, @RequestParam(name = "page") int page, @RequestParam(name = "size") int size) {
        return this.locationService.findByVin(vin, new PageRequest(page, size));
    }

}
```

***


## Database - H2 Database (In memory)

```xml
  <dependency>
      <groupId>com.h2database</groupId>
      <artifactId>h2</artifactId>
      <scope>runtime</scope>
  </dependency>
```

## External Configuration (.yml)

```yml
server:
  port: 9000
spring:
  application:
    name: fleet-location-service
```

### PostMan

- Post Request (With JSON mock Data)
```
http://localhost:9000/fleet
```

- Get Request
```
http://localhost:9000/fleet/STOPPED?page=0&size=10
```

```json
{
    "content": [
        {
            "vin": "7c08973d-bed4-4cbd-9c28-9282a02a6032",
            "id": 1,
            "unitInfo": {
                "unitVin": "7c08973d-bed4-4cbd-9c28-9282a02a6032",
                "engineMake": "DET",
                "customerName": "Koss and Sons",
                "unitNumber": "22832911"
            },
            "unitFault": {
                "vin": "7c08973d-bed4-4cbd-9c28-9282a02a6032",
                "spn": 524287,
                "fmi": 31
            },
            "latitude": 38.9093216,
            "longitude": -77.0036435,
            "heading": "E",
            "gpsSpeed": 0,
            "gpsStatus": "OK",
            "odometer": 76056,
            "totalEngineTime": 2139.25,
            "totalIdleTime": 409,
            "totalFuelUsage": 9798.667824,
            "address": "270 New York Ave NE, Washington, DC, 20002, USA",
            "timestamp": 1435852175000,
            "tspProvider": "cyntrx",
            "vehicleMovementType": "STOPPED",
            "serviceType": "ServiceInfo"
        },
        {
            "vin": "07e8db69-99f2-4fe2-b65a-52fbbdf8c32c",
            "id": 2,
            "unitInfo": {
                "unitVin": "07e8db69-99f2-4fe2-b65a-52fbbdf8c32c"
            },
            "latitude": 39.927434,
            "longitude": -76.635816,
            "heading": "NE",
            "gpsSpeed": 0,
            "odometer": 39492,
            "totalEngineTime": 4189.45,
            "totalIdleTime": 107.8,
            "totalFuelUsage": 3823,
            "address": "2975 Cape Horn Rd, Red Lion, PA, 17356, US",
            "timestamp": 1435730112000,
            "tspProvider": "cyntrx",
            "vehicleMovementType": "STOPPED",
            "serviceType": "None"
        },
        {
            "vin": "902f1305-4929-4aa6-a616-7d33fd026d2d",
            "id": 3,
            "unitInfo": {
                "unitVin": "902f1305-4929-4aa6-a616-7d33fd026d2d"
            },
            "latitude": 39.279583,
            "longitude": -76.671627,
            "heading": "N",
            "gpsSpeed": 0,
            "odometer": 113895,
            "totalEngineTime": 4620.75,
            "totalIdleTime": 1724.65,
            "totalFuelUsage": 22205.5,
            "address": "410 S S Caton Ave Ave, Baltimore, MD, 21229, USA",
            "timestamp": 1435247317000,
            "tspProvider": "cyntrx",
            "vehicleMovementType": "STOPPED",
            "serviceType": "None"
        }
    ],
    "last": false,
    "totalPages": 126,
    "totalElements": 378,
    "size": 3,
    "number": 0,
    "sort": null,
    "numberOfElements": 3,
    "first": true
}
```