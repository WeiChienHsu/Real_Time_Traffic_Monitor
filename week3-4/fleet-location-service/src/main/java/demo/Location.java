package demo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@RequiredArgsConstructor
@Entity
public class Location {

    enum GpsStatus {
        EXCELLENT, OK, UNRELIABLE, BAD, NOFIX, UNKNOWN;
    }

    enum VehicleMovementType {
        STOPPED, IN_MOTION;

        public boolean isMoving() {
            return this != STOPPED;
        }
    }

    @Id
    @GeneratedValue
    private Long id;
    @Embedded
    @AttributeOverride(name = "engineMake", column = @Column(name = "unit_engine_make"))
    private final UnitInfo unitInfo;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "fmi", column = @Column(name = "unit_fmi")),
            @AttributeOverride(name = "spn", column = @Column(name = "unit_spn"))})
    private UnitFault unitFault;
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
    // TODO: why is this not part of UnitFault?
    @Embedded
    private FaultCode faultCode;

    @SuppressWarnings("unused")
    private Location() {
        this.unitInfo = null;
    }

    @JsonCreator
    private Location(@JsonProperty("vin") String vin) {
        this.unitInfo = new UnitInfo(vin);
    }

    public String getVin() {
        return this.unitInfo == null ? null : this.unitInfo.getUnitVin();
    }

}
