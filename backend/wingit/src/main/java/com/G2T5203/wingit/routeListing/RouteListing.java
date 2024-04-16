package com.G2T5203.wingit.routeListing;

import com.G2T5203.wingit.booking.Booking;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

@Entity
public class RouteListing {
    @EmbeddedId
    private RouteListingPk routeListingPk; // Embedded composite key

    @DecimalMin(value = "100.0", message = "The minimum basePrice is $100.00")
    private double basePrice;

    @OneToMany(mappedBy = "outboundRouteListing", cascade = CascadeType.ALL)
//    @JsonManagedReference
    @JsonIgnore
    private List<Booking> outboundBooking;

    @OneToMany(mappedBy = "inboundRouteListing", cascade = CascadeType.ALL)
//    @JsonManagedReference
    @JsonIgnore
    private List<Booking> inboundBooking;

    public RouteListing(RouteListingPk routeListingPk, double basePrice) {
        this.routeListingPk = routeListingPk;
        this.basePrice = basePrice;
    }

    public RouteListing() {
    }

    public RouteListingPk getRouteListingPk() {
        return routeListingPk;
    }

    public void setRouteListingPk(RouteListingPk routeListingPk) {
        this.routeListingPk = routeListingPk;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public List<Booking> getOutboundBooking() {
        return outboundBooking;
    }

    public void setOutboundBooking(List<Booking> outboundBooking) {
        this.outboundBooking = outboundBooking;
    }

    public List<Booking> getInboundBooking() {
        return inboundBooking;
    }

    public void setInboundBooking(List<Booking> inboundBooking) {
        this.inboundBooking = inboundBooking;
    }

    @Override
    public String toString() {
        return "RouteListing{" +
                "routeListingPk=" + routeListingPk +
                ", basePrice=" + basePrice +
                '}';
    }
}
