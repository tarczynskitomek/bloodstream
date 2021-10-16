package it.tarczynski.bloodstream.vehicle.infrastructure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import java.time.Duration;

@ConstructorBinding
@ConfigurationProperties(prefix = "public-transport-api")
public class TransportApiConfigurationProperties {

    private final String apiKey;
    private final String resourceId;
    private final Connection connection;

    public TransportApiConfigurationProperties(String apiKey,
                                               String resourceId,
                                               Connection connection) {
        this.apiKey = apiKey;
        this.resourceId = resourceId;
        this.connection = connection;
    }

    public static class Connection {

        private final String url;
        private final int requestTimeoutMillis;

        public Connection(String url,
                          int requestTimeoutMillis) {
            this.url = url;
            this.requestTimeoutMillis = requestTimeoutMillis;
        }
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getResourceId() {
        return resourceId;
    }

    public Duration requestTimeout() {
        return Duration.ofMillis(connection.requestTimeoutMillis);
    }

    public String uri() {
        return connection.url;
    }
}
