package it.tarczynski.bloodstream.vehicle.infrastructure;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

public interface TimeMachine {

    default Instant now() {
        return Instant.now();
    }

    default Instant nowMinus(Duration duration) {
        return now().minus(duration);
    }
}

@Component
class JavaTimeMachine implements TimeMachine {
}
