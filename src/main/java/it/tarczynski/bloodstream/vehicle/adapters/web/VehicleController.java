package it.tarczynski.bloodstream.vehicle.adapters.web;

import it.tarczynski.bloodstream.vehicle.domain.Vehicle;
import it.tarczynski.bloodstream.vehicle.domain.VehicleService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequestMapping("/vehicles")
class VehicleController {

    private final VehicleService vehicleService;

    VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @GetMapping(
            path = "/{type}",
            produces = {MediaType.TEXT_EVENT_STREAM_VALUE}
    )
    public Flux<Vehicle> vehicle(@PathVariable Vehicle.Type type,
                                 @RequestParam List<String> lines,
                                 @RequestParam(defaultValue = "true") boolean allowStalled) {
        return vehicleService.getPositionsOf(type, lines, allowStalled);
    }

    @GetMapping(
            path = "/active",
            produces = {MediaType.TEXT_EVENT_STREAM_VALUE}
    )
    public Flux<Long> activeCount() {
        return vehicleService.getActiveCount();
    }
}
