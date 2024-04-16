package com.G2T5203.wingit.routeListing;

import java.time.Duration;
import java.time.LocalDateTime;

public class RouteListingSimpleJson {
    private Integer routeId;
    private String planeId;
    private LocalDateTime departureDatetime;
    private Duration flightDuration;
    private double basePrice;
    private int availableSeats;

    public RouteListingSimpleJson(Integer routeId, String planeId, LocalDateTime departureDatetime, Duration flightDuration, double basePrice, int availableSeats) {
        this.routeId = routeId;
        this.planeId = planeId;
        this.departureDatetime = departureDatetime;
        this.flightDuration = flightDuration;
        this.basePrice = basePrice;
        this.availableSeats = availableSeats;
    }

    public RouteListingSimpleJson(RouteListing routeListing, int availableSeats) {
        this(
                routeListing.getRouteListingPk().getRoute().getRouteId(),
                routeListing.getRouteListingPk().getPlane().getPlaneId(),
                routeListing.getRouteListingPk().getDepartureDatetime(),
                routeListing.getRouteListingPk().getRoute().getFlightDuration(),
                routeListing.getBasePrice(),

                availableSeats
        );
    }
    public RouteListingSimpleJson() {}

    public Integer getRouteId() {
        return routeId;
    }

    public void setRouteId(Integer routeId) {
        this.routeId = routeId;
    }

    public String getPlaneId() {
        return planeId;
    }

    public void setPlaneId(String planeId) {
        this.planeId = planeId;
    }

    public LocalDateTime getDepartureDatetime() {
        return departureDatetime;
    }

    public void setDepartureDatetime(LocalDateTime departureDatetime) {
        this.departureDatetime = departureDatetime;
    }

    public Duration getFlightDuration() { return flightDuration; }

    public void setFlightDuration(Duration flightDuration) { this.flightDuration = flightDuration; }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public int getAvailableSeats() { return availableSeats; }

    public void setAvailableSeats(int availableSeats) { this.availableSeats = availableSeats; }
}
