package it.tarczynski.bloodstream.vehicle.adapters.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.tarczynski.bloodstream.vehicle.domain.Vehicle;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static it.tarczynski.bloodstream.vehicle.domain.ProjectConstants.WARSAW_ZONE_ID;
import static java.time.format.DateTimeFormatter.ofPattern;

record ZtmVehiclesJson(
        @JsonProperty("result") List<ZtmVehicle> vehicles
) {

    public static final ZtmVehiclesJson NO_VEHICLES = new ZtmVehiclesJson(List.of());

    record ZtmVehicle(
            @JsonProperty("Lines") String line,
            @JsonProperty("Brigade") String brigade,
            @JsonProperty("VehicleNumber") String vehicleNumber,
            @JsonProperty("Lat") String latitude,
            @JsonProperty("Lon") String longitude,
            @JsonProperty("Time") String lastSeenAt
    ) {

        public static final DateTimeFormatter DATE_TIME_FORMATTER = ofPattern("yyyy-MM-dd HH:mm:ss");

        Vehicle toVehicle(Vehicle.Type type) {
            String id = String.format("%s_%s", line, brigade);
            return new Vehicle(
                    id, line, brigade, vehicleNumber, new Vehicle.Position(latitude, longitude), parseLastSeen(), type
            );
        }

        private Instant parseLastSeen() {
            var dateTime = LocalDateTime.parse(this.lastSeenAt, DATE_TIME_FORMATTER);
            return ZonedDateTime.of(dateTime, WARSAW_ZONE_ID).toInstant();
        }
    }
}
