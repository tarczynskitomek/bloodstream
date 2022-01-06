package it.tarczynski.bloodstream.vehicle.adapters.client;

import it.tarczynski.bloodstream.vehicle.domain.Vehicle;
import it.tarczynski.bloodstream.vehicle.domain.VehicleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.function.Function;

@Component
class VehiclePoller {

    private static final Logger log = LoggerFactory.getLogger(VehiclePoller.class);

    private final VehicleApiClient apiClient;
    private final VehicleRepository vehicleRepository;

    private Disposable pollingSubscription;

    VehiclePoller(VehicleApiClient apiClient,
                  VehicleRepository vehicleRepository) {
        this.apiClient = apiClient;
        this.vehicleRepository = vehicleRepository;
    }

    @PostConstruct
    void doPoll() {
        this.pollingSubscription = Flux.interval(Duration.ZERO, Duration.ofSeconds(10L))
                .doOnSubscribe(s -> log.info("Vehicle polling started at [{}]", Instant.now()))
                .flatMap(i -> fetchVehiclePositionSnapshot())
                .subscribe(batch -> log.info("[{}] vehicles moved during last 10 seconds", batch.size()));
    }

    @PreDestroy
    void cancelPoll() {
        // allow MongoDB Connection pool graceful shutdown
        pollingSubscription.dispose();
    }

    private Mono<List<Vehicle>> fetchVehiclePositionSnapshot() {
        return fetchCurrentSnapshot()
                .filterWhen(this::hasMoved)
                .doOnNext(vehicle -> log.debug("About to save vehicle [{}]", vehicle))
                .collectList()
                .flatMap(vehicleRepository::saveAll);
    }

    private Mono<Boolean> hasMoved(Vehicle vehicle) {
        return vehicleRepository.getById(vehicle.id())
                .map(existing -> existing.hasMoved(vehicle.position()))
                .defaultIfEmpty(true);
    }

    private Flux<Vehicle> fetchCurrentSnapshot() {
        return Flux.merge(
                fetchByType(Vehicle.Type.BUS),
                fetchByType(Vehicle.Type.TRAM)
        );
    }

    private Flux<Vehicle> fetchByType(Vehicle.Type type) {
        return apiClient.fetchByType(type)
                .map(ZtmVehiclesJson::vehicles)
                .doOnError(e -> log.warn("Failed to fetch vehicles [{}]", type, e))
                .onErrorReturn(List.of())
                .flatMapIterable(Function.identity())
                .map(json -> json.toVehicle(type));
    }

}
