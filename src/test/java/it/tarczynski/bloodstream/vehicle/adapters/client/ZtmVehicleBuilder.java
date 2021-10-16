package it.tarczynski.bloodstream.vehicle.adapters.client;

import it.tarczynski.bloodstream.vehicle.domain.Vehicle;

public class ZtmVehicleBuilder {

    private final Vehicle.Type type;

    private String line = "222";
    private String brigade = "1";
    private String vehicleNumber = "2244";
    private String latitude = "52.23682";
    private String longitude = "21.018042";
    private String lastSeenAt = "2021-10-13 12:03:28";

    public ZtmVehicleBuilder(Vehicle.Type type) {
        this.type = type;
    }

    public static ZtmVehicleBuilder bus() {
        return new ZtmVehicleBuilder(Vehicle.Type.BUS);
    }

    public static ZtmVehicleBuilder tram() {
        return new ZtmVehicleBuilder(Vehicle.Type.TRAM);
    }

    public ZtmVehicleBuilder withLine(String line) {
        this.line = line;
        return this;
    }

    public ZtmVehicleBuilder withBrigade(String brigade) {
        this.brigade = brigade;
        return this;
    }

    public ZtmVehicleBuilder withVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
        return this;
    }

    public ZtmVehicleBuilder withLatitude(String latitude) {
        this.latitude = latitude;
        return this;
    }

    public ZtmVehicleBuilder withLongitude(String longitude) {
        this.longitude = longitude;
        return this;
    }

    public ZtmVehicleBuilder withLastSeenAt(String lastSeenAt) {
        this.lastSeenAt = lastSeenAt;
        return this;
    }

    public ZtmVehiclesJson.ZtmVehicle build() {
        return new ZtmVehiclesJson.ZtmVehicle(
                line, brigade, vehicleNumber, latitude, longitude, lastSeenAt
        );
    }
}
