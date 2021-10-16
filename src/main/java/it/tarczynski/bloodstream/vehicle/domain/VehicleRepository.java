package it.tarczynski.bloodstream.vehicle.domain;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

public interface VehicleRepository {

    Mono<List<Vehicle>> saveAll(List<Vehicle> vehicles);

    Mono<Vehicle> getById(String id);

    Flux<Vehicle> positionsOf(Vehicle.Type type, String line);

    Flux<Vehicle> positionsOf(Vehicle.Type type, List<String> lines);

    Flux<Long> countActiveDuringLast(Duration minutes);
}
