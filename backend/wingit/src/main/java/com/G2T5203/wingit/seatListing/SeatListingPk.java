package com.G2T5203.wingit.seatListing;

import com.G2T5203.wingit.routeListing.RouteListing;
import com.G2T5203.wingit.routeListing.RouteListingPk;
import com.G2T5203.wingit.seat.Seat;
import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Embeddable
public class SeatListingPk implements Serializable {
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "routePlaneId", referencedColumnName = "planeId"),
            @JoinColumn(name = "routeId", referencedColumnName = "routeId"),
            @JoinColumn(name = "departureDatetime", referencedColumnName = "departureDatetime")
    })
    private RouteListing routeListing;
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "seatPlaneId", referencedColumnName = "planeId"),
            @JoinColumn(name = "seatNumber", referencedColumnName = "seatNumber"),
    })
    private Seat seat;

    public SeatListingPk(RouteListing routeListing, Seat seat) {
        this.routeListing = routeListing;
        this.seat = seat;
    }

    // Method for BookingSimpleJson constructor to filter seat listings to a list
    // Sorted by either outbound and inbound route listings (planeId, routeId, departureDatetime)
    // Return boolean to check if that particular seat matches the given details above
    public boolean checkSeatBelongsToRouteListing(SeatListing seatListing, RouteListingPk routeListingPk) {
        // outbound/inbound route listing's PK, to check against this seatListing's route listing
        String routePlaneId = routeListingPk.getPlane().getPlaneId();
        int routeRouteId = routeListingPk.getRoute().getRouteId();
        LocalDateTime routeDepartureDatetime = routeListingPk.getDepartureDatetime();

        // this seatListing's route listing PK
        String planeId = seatListing.getSeatListingPk().getRouteListing().getRouteListingPk().getPlane().getPlaneId();
        int routeId = seatListing.getSeatListingPk().getRouteListing().getRouteListingPk().getRoute().getRouteId();
        LocalDateTime departureDatetime = seatListing.getSeatListingPk().getRouteListing().getRouteListingPk().getDepartureDatetime();

        // compare
        if (planeId.equals(routePlaneId)) {
            if (routeId == routeRouteId) {
                if (departureDatetime.equals(routeDepartureDatetime)) {
                    return true;
                }
            }
        }

        return false;
    }

    public SeatListingPk() {}

    public RouteListing getRouteListing() {
        return routeListing;
    }

    public void setRouteListing(RouteListing routeListing) {
        this.routeListing = routeListing;
    }

    public Seat getSeat() {
        return seat;
    }

    public void setSeat(Seat seat) {
        this.seat = seat;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
