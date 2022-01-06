package it.tarczynski.bloodstream.test;

import com.google.common.net.HostAndPort;
import org.testcontainers.containers.GenericContainer;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Objects;

public class Docker {

    public static Mono<Void> start() {
        return Mono.fromCallable(() ->
                startAsyncMongo()
                        .doOnNext(addr -> configure(addr, "spring.data.mongodb"))
        ).then();
    }

    private static void configure(HostAndPort addr, String configPrefix) {
        System.setProperty(configPrefix + ".host", addr.getHost());
        System.setProperty(configPrefix + ".port", String.valueOf(addr.getPort()));
    }

    private static Mono<HostAndPort> startAsyncMongo() {
        return Mono.fromCallable(() -> {
            var container = new GenericContainer("mongo:4.0.5");
            container.addExposedPort(27017);
            container.withReuse(true).start();
            return HostAndPort.fromParts(
                    container.getContainerIpAddress(),
                    Objects.requireNonNull(container.getMappedPort(27017), "No mapping for port " + 27017)
            );
        }).subscribeOn(Schedulers.boundedElastic());
    }

}
