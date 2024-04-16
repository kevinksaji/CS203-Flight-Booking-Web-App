package com.G2T5203.wingit.seatListing;

import java.time.LocalDateTime;

public class PrivacySeatListingSimpleJson {
    private String planeId;
    public int routeId;
    public LocalDateTime departureDatetime;
    public String seatNumber;
    public String seatClass;
    public double seatPrice;
    public boolean isBooked;


    public PrivacySeatListingSimpleJson(String planeId, int routeId, LocalDateTime departureDatetime, String seatNumber, String seatClass, double seatPrice, boolean isBooked) {
        this.planeId = planeId;
        this.routeId = routeId;
        this.departureDatetime = departureDatetime;
        this.seatNumber = seatNumber;
        this.seatClass = seatClass;
        this.seatPrice = seatPrice;
        this.isBooked = isBooked;
    }

    public PrivacySeatListingSimpleJson(SeatListing seatListing) {
        this(
                seatListing.getSeatListingPk().getRouteListing().getRouteListingPk().getPlane().getPlaneId(),
                seatListing.getSeatListingPk().getRouteListing().getRouteListingPk().getRoute().getRouteId(),
                seatListing.getSeatListingPk().getRouteListing().getRouteListingPk().getDepartureDatetime(),
                seatListing.getSeatListingPk().getSeat().getSeatPk().getSeatNumber(),
                seatListing.getSeatListingPk().getSeat().getSeatClass(),
                seatListing.getSeatListingPk().getRouteListing().getBasePrice() * seatListing.getSeatListingPk().getSeat().getPriceFactor(), // Can't be computed from seatListing itself.
                seatListing.getBooking() != null);
    }

    public String getPlaneId() {
        return planeId;
    }

    public void setPlaneId(String planeId) {
        this.planeId = planeId;
    }

    public int getRouteId() {
        return routeId;
    }

    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }

    public LocalDateTime getDepartureDatetime() {
        return departureDatetime;
    }

    public void setDepartureDatetime(LocalDateTime departureDatetime) {
        this.departureDatetime = departureDatetime;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public String getSeatClass() { return seatClass; }

    public void setSeatClass(String seatClass) { this.seatClass = seatClass; }

    public double getSeatPrice() { return seatPrice; }

    public void setSeatPrice(double seatPrice) { this.seatPrice = seatPrice; }

    public boolean getIsBooked() { return isBooked; }

    public void setIsBooked(boolean booked) { isBooked = booked; }
}
