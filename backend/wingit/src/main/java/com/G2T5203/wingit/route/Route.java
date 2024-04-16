package com.G2T5203.wingit.route;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.time.DurationMin;

import java.time.Duration;

@Entity
public class Route {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer routeId;
    @NotEmpty
    private String departureDest;
    @NotEmpty
    private String arrivalDest;
    @DurationMin(hours = 1, message = "The minimum flight duration is 1h")
    private Duration flightDuration;

    public Route(Integer routeId, String departureDest, String arrivalDest, Duration flightDuration) {
        this.routeId = routeId;
        this.departureDest = departureDest;
        this.arrivalDest = arrivalDest;
        this.flightDuration = flightDuration;
    }

    public Route(String departureDest, String arrivalDest, Duration flightDuration) {
        this.departureDest = departureDest;
        this.arrivalDest = arrivalDest;
        this.flightDuration = flightDuration;
    }

    public Route() {
    }

    public Integer getRouteId() {
        return routeId;
    }

    public void setRouteId(Integer routeId) {
        this.routeId = routeId;
    }

    public String getDepartureDest() {
        return departureDest;
    }

    public void setDepartureDest(String departureDest) {
        this.departureDest = departureDest;
    }

    public String getArrivalDest() {
        return arrivalDest;
    }

    public void setArrivalDest(String arrivalDest) {
        this.arrivalDest = arrivalDest;
    }

    public Duration getFlightDuration() {
        return flightDuration;
    }

    public void setFlightDuration(Duration flightDuration) {
        this.flightDuration = flightDuration;
    }

    @Override
    public String toString() {
        return "Route{" +
                "routeId='" + routeId + '\'' +
                ", departureDest='" + departureDest + '\'' +
                ", arrivalDest='" + arrivalDest + '\'' +
                ", flightDuration=" + flightDuration +
                '}';
    }
}
