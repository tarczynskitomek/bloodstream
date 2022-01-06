package it.tarczynski.bloodstream.vehicle.adapters.db;

import it.tarczynski.bloodstream.vehicle.domain.Vehicle;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

interface ReactiveVehicleRepository extends ReactiveMongoRepository<Vehicle, String> {
}
