package it.tarczynski.bloodstream.vehicle.adapters.client;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static it.tarczynski.bloodstream.vehicle.adapters.client.ZtmVehicleBuilder.bus;
import static it.tarczynski.bloodstream.vehicle.adapters.client.ZtmVehiclesJsonBuilder.ztmVehicles;
import static it.tarczynski.bloodstream.vehicle.domain.Vehicle.Type.BUS;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

@WireMockTest(httpPort = 8000)
class VehicleApiClientSpec extends BaseIntegrationSpec {

    @Autowired
    private VehicleApiClient apiClient;

    @Test
    void should_return_mono_with_api_response() {
        // given
        stubOk(BUS);

        // when
        Mono<ZtmVehiclesJson> vehicles = apiClient.fetchByType(BUS);

        // expect
        vehicles.as(StepVerifier::create)
                .expectNext(ztmVehicles()
                        .addVehicle(
                                bus()
                                        .withVehicleNumber("1906")
                                        .withBrigade("1")
                                        .withLastSeenAt("2021-10-13 12:03:28")
                                        .withLatitude("52.186728")
                                        .withLongitude("20.996745")
                                        .build()
                        )
                        .addVehicle(
                                bus()
                                        .withVehicleNumber("1907")
                                        .withBrigade("2")
                                        .withLastSeenAt("2021-10-13 12:03:28")
                                        .withLatitude("52.193153")
                                        .withLongitude("21.013793")
                                        .build()
                        )
                        .addVehicle(
                                bus()
                                        .withVehicleNumber("1909")
                                        .withBrigade("5")
                                        .withLastSeenAt("2021-10-13 12:03:23")
                                        .withLatitude("52.2437083")
                                        .withLongitude("21.0020103")
                                        .build()
                        )
                        .build())
                .expectComplete()
                .verify();
    }

    @Test
    void should_return_a_mono_with_empty_list_when_server_error() {
        // given
        stubError(SERVICE_UNAVAILABLE);

        // when
        Mono<ZtmVehiclesJson> vehicles = apiClient.fetchByType(BUS);

        // then
        vehicles.as(StepVerifier::create)
                .expectNext(ztmVehicles().build())
                .expectComplete()
                .verify();
    }

    @Test
    void should_retry_three_times_on_error() {
        // given
        stubError(INTERNAL_SERVER_ERROR);

        // when
        apiClient.fetchByType(BUS).block();

        // then
        WireMock.verify(4, RequestPatternBuilder.allRequests());
    }

    @Test
    void should_timeout_after_five_seconds_and_return_empty_vehicles() {
        // given
        stubSlow(Duration.ofSeconds(5));

        // expect
        apiClient.fetchByType(BUS)
                .as(StepVerifier::create)
                .expectNext(ZtmVehiclesJson.NO_VEHICLES)
                .expectComplete()
                .verify();
    }
}
