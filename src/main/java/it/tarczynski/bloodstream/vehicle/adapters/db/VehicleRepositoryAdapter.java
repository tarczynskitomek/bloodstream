package it.tarczynski.bloodstream.vehicle.adapters.db;

import it.tarczynski.bloodstream.vehicle.domain.Vehicle;
import it.tarczynski.bloodstream.vehicle.domain.VehicleRepository;
import it.tarczynski.bloodstream.vehicle.infrastructure.TimeMachine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.ChangeStreamEvent;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Component
class VehicleRepositoryAdapter implements VehicleRepository {

    private static final Logger log = LoggerFactory.getLogger(VehicleRepositoryAdapter.class);

    private final ReactiveVehicleRepository delegate;
    private final ReactiveMongoTemplate mongoTemplate;
    private final TimeMachine timeMachine;

    VehicleRepositoryAdapter(ReactiveVehicleRepository delegate,
                             ReactiveMongoTemplate mongoTemplate,
                             TimeMachine timeMachine) {
        this.delegate = delegate;
        this.mongoTemplate = mongoTemplate;
        this.timeMachine = timeMachine;
    }

    @Override
    public Mono<List<Vehicle>> saveAll(List<Vehicle> vehicles) {
        return delegate.saveAll(vehicles).collectList();
    }

    @Override
    public Mono<Vehicle> getById(String id) {
        return delegate.findById(id);
    }

    @Override
    public Flux<Vehicle> positionsOf(Vehicle.Type type, String line) {
        return positionsOf(type, List.of(line));
    }

    @Override
    public Flux<Vehicle> positionsOf(Vehicle.Type type, List<String> lines) {
        Flux<Vehicle> snapshot = getSnapshot(type, lines);
        Flux<Vehicle> changes = getChangeStream(type, lines);
        return Flux.merge(snapshot, changes)
                .doOnSubscribe(s -> log.info("Positions subscribed for [{}] and lines {}", type, lines))
                .doOnCancel(() -> log.info("Subscription to [{}] lines {} cancelled", type, lines));
    }

    @Override
    public Flux<Long> countActiveDuringLast(Duration duration) {
        return Flux.interval(Duration.ZERO, Duration.ofMinutes(10L))
                .flatMap(i -> mongoTemplate.count(
                        query(
                                where("lastModified")
                                        .gte(timeMachine.nowMinus(duration))
                        ),
                        Vehicle.class
                ))
                .cache(1);
    }

    private Flux<Vehicle> getChangeStream(Vehicle.Type type, List<String> lines) {
        return mongoTemplate.changeStream(Vehicle.class)
                .filter(
                        where("type")
                                .is(type)
                                .and("line")
                                .in(lines)
                )
                .listen()
                .filter(event -> event.getBody() != null)
                .map(ChangeStreamEvent::getBody);
    }

    private Flux<Vehicle> getSnapshot(Vehicle.Type type, List<String> lines) {
        return mongoTemplate.find(
                query(
                        where("type")
                                .is(type)
                                .and("line")
                                .in(lines)
                ), Vehicle.class
        );
    }
}
