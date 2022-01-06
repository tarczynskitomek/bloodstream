package it.tarczynski.bloodstream;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

import static it.tarczynski.bloodstream.vehicle.domain.ProjectConstants.WARSAW_ZONE_ID;

@SpringBootApplication
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @PostConstruct
    void setup() {
        TimeZone.setDefault(TimeZone.getTimeZone(WARSAW_ZONE_ID));
    }
}
