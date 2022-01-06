package it.tarczynski.bloodstream.vehicle.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Duration;
import java.time.Instant;

@Document("vehicles")
public record Vehicle(
        @Id String id,
        @Indexed String line,
        String brigade,
        String vehicleNumber,
        Position position,
        Instant lastModified,
        @Indexed Type type) {

    public boolean hasMoved(Position position) {
        return !this.position.equals(position);
    }

    public static record Position(String latitude, String longitude) {
    }

    public enum Type {
        BUS(1), TRAM(2);

        private final int code;

        Type(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }

    @JsonProperty("stalled")
    public boolean stalled() {
        return Duration.between(lastModified, Instant.now())
                .compareTo(Duration.ofMinutes(10)) > 0;
    }

}
    
