package it.tarczynski.bloodstream.vehicle.adapters.client;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import it.tarczynski.bloodstream.test.Docker;
import it.tarczynski.bloodstream.vehicle.domain.Vehicle;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.serverError;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static it.tarczynski.bloodstream.vehicle.domain.Vehicle.Type.BUS;
import static it.tarczynski.bloodstream.vehicle.domain.Vehicle.Type.TRAM;

@SpringBootTest
public class BaseIntegrationSpec {

    private static final Map<Vehicle.Type, String> RESPONSES;

    @BeforeAll
    public static void initDocker() {
        Docker.start();
    }

    @BeforeEach
    public void setup() {
        WireMock.reset();
    }

    static {
        try {
            RESPONSES = Map.of(
                    BUS, String.join("\n", Files.readAllLines(Paths.get("src/test/resources/response_type_1.json"))),
                    TRAM, String.join("\n", Files.readAllLines(Paths.get("src/test/resources/response_type_2.json")))
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private MappingBuilder getVehiclesMappingFor(Vehicle.Type type, Duration delay) {
        return get(String.format("/vehicles?resource_id=fake_id&apikey=fake_key&type=%d", type.getCode()))
                .withHeader("Accept", matching("application/json"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(RESPONSES.get(type))
                        .withFixedDelay(Long.valueOf(delay.toMillis()).intValue())
                );
    }

    protected void stubOk(Vehicle.Type type) {
        stubFor(
                getVehiclesMappingFor(type, Duration.ZERO)
        );
    }

    protected void stubError(HttpStatus status) {
        stubFor(
                get(UrlPattern.ANY)
                        .willReturn(serverError()
                                .withStatus(status.value()))
        );
    }

    protected void stubSlow(Duration duration) {
        stubFor(
                getVehiclesMappingFor(BUS, duration)
        );
    }
}
