package it.tarczynski.bloodstream.vehicle.adapters.client;

import java.util.ArrayList;
import java.util.List;

public class ZtmVehiclesJsonBuilder {

    private final List<ZtmVehiclesJson.ZtmVehicle> vehicles = new ArrayList<>();

    public static ZtmVehiclesJsonBuilder ztmVehicles() {
        return new ZtmVehiclesJsonBuilder();
    }

    public ZtmVehiclesJsonBuilder addVehicle(ZtmVehiclesJson.ZtmVehicle vehicle) {
        this.vehicles.add(vehicle);
        return this;
    }

    public ZtmVehiclesJson build() {
        return new ZtmVehiclesJson(vehicles);
    }
}
