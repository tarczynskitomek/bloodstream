package it.tarczynski.bloodstream.vehicle.infrastructure;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

import java.time.Duration;

@Configuration
@EnableConfigurationProperties(TransportApiConfigurationProperties.class)
public class Config {

    @Bean
    WebClient webClient(TransportApiConfigurationProperties configuration) {
        return WebClient
                .builder()
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create()
                                .followRedirect(true)
                                .responseTimeout(configuration.requestTimeout())
                ))
                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(1024 * 1024 * 10))
                .build();
    }

    @Bean
    RetryBackoffSpec retrySpec() {
        return Retry.backoff(3, Duration.ofMillis(100))
                .jitter(0.5)
                .maxBackoff(Duration.ofSeconds(2));
    }

    @Profile("local")
    @Configuration
    static class LocalCORSConfig implements WebFluxConfigurer {

        @Override
        public void addCorsMappings(CorsRegistry registry) {
            registry.addMapping("/**")
                    .allowedMethods("GET")
                    .allowedOrigins("*");
        }
    }
}
