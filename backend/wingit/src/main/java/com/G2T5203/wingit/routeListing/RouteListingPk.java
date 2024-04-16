package com.G2T5203.wingit.routeListing;

import com.G2T5203.wingit.plane.Plane;
import com.G2T5203.wingit.route.Route;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotEmpty;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Embeddable
public class RouteListingPk implements Serializable {
    @ManyToOne
    @JoinColumn(name = "planeId")
    private Plane plane;

    @ManyToOne
    @JoinColumn(name = "routeId")
    private Route route;
    private LocalDateTime departureDatetime;

    public RouteListingPk(Plane plane, Route route, LocalDateTime departureDatetime) {
        this.plane = plane;
        this.route = route;
        this.departureDatetime = departureDatetime;
    }

    public RouteListingPk() {}

    public Plane getPlane() {
        return plane;
    }

    public void setPlane(Plane plane) {
        this.plane = plane;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public LocalDateTime getDepartureDatetime() {
        return departureDatetime;
    }

    public void setDepartureDatetime(LocalDateTime departureDatetime) {
        this.departureDatetime = departureDatetime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RouteListingPk that = (RouteListingPk) o;
        return Objects.equals(plane, that.plane) && Objects.equals(route, that.route) && Objects.equals(departureDatetime, that.departureDatetime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(plane, route, departureDatetime);
    }
}
