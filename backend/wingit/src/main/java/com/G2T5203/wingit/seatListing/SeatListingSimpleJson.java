package com.G2T5203.wingit.seatListing;

import java.time.LocalDateTime;

public class SeatListingSimpleJson {
    private String planeId;
    public int routeId;
    public LocalDateTime departureDatetime;
    public String seatNumber;
    public String seatClass;
    public Integer bookingId;
    public String occupantName;

    public SeatListingSimpleJson(String planeId, int routeId, LocalDateTime departureDatetime, String seatNumber, String seatClass, Integer bookingId, String occupantName) {
        this.planeId = planeId;
        this.routeId = routeId;
        this.departureDatetime = departureDatetime;
        this.seatNumber = seatNumber;
        this.seatClass = seatClass;
        this.bookingId = bookingId;
        this.occupantName = occupantName;
    }

    public SeatListingSimpleJson(SeatListing seatListing) {
        this(
                seatListing.getSeatListingPk().getRouteListing().getRouteListingPk().getPlane().getPlaneId(),
                seatListing.getSeatListingPk().getRouteListing().getRouteListingPk().getRoute().getRouteId(),
                seatListing.getSeatListingPk().getRouteListing().getRouteListingPk().getDepartureDatetime(),
                seatListing.getSeatListingPk().getSeat().getSeatPk().getSeatNumber(),
                seatListing.getSeatListingPk().getSeat().getSeatClass(),
                seatListing.getBooking() != null ? seatListing.getBooking().getBookingId() : null,
                seatListing.getOccupantName());
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

    public Integer getBookingId() {
        return bookingId;
    }

    public void setBookingId(Integer bookingId) {
        this.bookingId = bookingId;
    }

    public String getOccupantName() {
        return occupantName;
    }

    public void setOccupantName(String occupantName) {
        this.occupantName = occupantName;
    }
}
