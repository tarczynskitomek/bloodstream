package it.tarczynski.bloodstream.vehicle.domain;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;

@Component
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public Flux<Vehicle> getPositionsOf(Vehicle.Type type,
                                        List<String> lines,
                                        boolean allowStalled) {
        return vehicleRepository
                .positionsOf(type, lines)
                .filter(vehicle -> allowStalled || !vehicle.stalled());
    }

    public Flux<Long> getActiveCount() {
        return vehicleRepository.countActiveDuringLast(Duration.ofMinutes(10));
    }
}
