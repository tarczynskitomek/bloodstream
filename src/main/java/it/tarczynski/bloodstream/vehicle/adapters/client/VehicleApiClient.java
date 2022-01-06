package it.tarczynski.bloodstream.vehicle.adapters.client;

import it.tarczynski.bloodstream.vehicle.domain.Vehicle.Type;
import it.tarczynski.bloodstream.vehicle.infrastructure.TransportApiConfigurationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.util.retry.RetryBackoffSpec;

import java.time.Duration;

import static it.tarczynski.bloodstream.vehicle.adapters.client.ZtmVehiclesJson.NO_VEHICLES;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Component
class VehicleApiClient {

    private static final Logger log = LoggerFactory.getLogger(VehicleApiClient.class);

    private final WebClient webClient;
    private final TransportApiConfigurationProperties configuration;
    private final RetryBackoffSpec retrySpec;

    VehicleApiClient(WebClient webClient,
                     TransportApiConfigurationProperties configuration,
                     RetryBackoffSpec retrySpec) {
        this.webClient = webClient;
        this.configuration = configuration;
        this.retrySpec = retrySpec;
    }

    Mono<ZtmVehiclesJson> fetchByType(Type type) {
        return webClient.get()
                .uri(uri(type))
                .accept(APPLICATION_JSON)
                .retrieve()
                .bodyToMono(ZtmVehiclesJson.class)
                .retryWhen(retrySpec)
                .timeout(Duration.ofSeconds(5))
                .onErrorReturn(NO_VEHICLES)
                .doOnNext(json -> log.debug("Fetched vehicle JSON [{}]", json))
                .doOnSubscribe(s -> log.info("Fetch started for vehicle type [{}]", type))
                ;
    }

    private String uri(Type type) {
        return UriComponentsBuilder.fromHttpUrl(configuration.uri())
                .queryParam("resource_id", configuration.getResourceId())
                .queryParam("apikey", configuration.getApiKey())
                .queryParam("type", type.getCode())
                .build()
                .toUriString();
    }
}
