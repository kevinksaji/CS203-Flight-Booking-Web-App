package com.G2T5203.wingit.booking;

import com.G2T5203.wingit.seatListing.SeatListing;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

public class BookingSimpleJson {
    private Integer bookingId;
    private String username;

    // Outbound Routelisting
    private String outboundPlaneId;
    private int outboundRouteId;
    private String outboundDepartureDestination;
    private String outboundArrivalDestination;
    private LocalDateTime outboundDepartureDatetime;

    // Inbound Routelisting
    private String inboundPlaneId;
    private int inboundRouteId;
    private String inboundDepartureDestination;
    private String inboundArrivalDestination;
    private LocalDateTime inboundDepartureDatetime;

    // The rest
    private LocalDateTime startBookingDatetime;
    private int partySize;
    private double chargedPrice;
    private boolean isPaid;

    // SeatListing, just a Json list of the seat numbers for each route listing
    private Map<String, String> outboundSeatNumbers;
    private Map<String, String> inboundSeatNumbers;


    public BookingSimpleJson(Integer bookingId, String username, String outboundPlaneId, int outboundRouteId, String outboundDepartureDestination, String outboundArrivalDestination, LocalDateTime outboundDepartureDatetime, String inboundPlaneId, int inboundRouteId, String inboundDepartureDestination, String inboundArrivalDestination, LocalDateTime inboundDepartureDatetime, LocalDateTime startBookingDatetime, int partySize, double chargedPrice, boolean isPaid, Map<String, String> outboundSeatNumbers, Map<String, String> inboundSeatNumbers) {
        this.bookingId = bookingId;
        this.username = username;

        this.outboundPlaneId = outboundPlaneId;
        this.outboundRouteId = outboundRouteId;
        this.outboundDepartureDestination = outboundDepartureDestination;
        this.outboundArrivalDestination = outboundArrivalDestination;
        this.outboundDepartureDatetime = outboundDepartureDatetime;

        this.inboundPlaneId = inboundPlaneId;
        this.inboundRouteId = inboundRouteId;
        this.inboundDepartureDestination = inboundDepartureDestination;
        this.inboundArrivalDestination = inboundArrivalDestination;
        this.inboundDepartureDatetime = inboundDepartureDatetime;

        this.startBookingDatetime = startBookingDatetime;
        this.partySize = partySize;
        this.chargedPrice = chargedPrice;
        this.isPaid = isPaid;

        this.outboundSeatNumbers = outboundSeatNumbers;
        this.inboundSeatNumbers = inboundSeatNumbers;
    }

    public BookingSimpleJson(Booking booking) {
        this(
                booking.getBookingId(),
                booking.getWingitUser().getUsername(),

                booking.getOutboundRouteListing().getRouteListingPk().getPlane().getPlaneId(),
                booking.getOutboundRouteListing().getRouteListingPk().getRoute().getRouteId(),
                booking.getOutboundRouteListing().getRouteListingPk().getRoute().getDepartureDest(),
                booking.getOutboundRouteListing().getRouteListingPk().getRoute().getArrivalDest(),
                booking.getOutboundRouteListing().getRouteListingPk().getDepartureDatetime(),

                booking.hasInboundRouteListing() ? booking.getInboundRouteListing().getRouteListingPk().getPlane().getPlaneId() : null,
                booking.hasInboundRouteListing() ? booking.getInboundRouteListing().getRouteListingPk().getRoute().getRouteId() : -1,
                booking.hasInboundRouteListing() ? booking.getInboundRouteListing().getRouteListingPk().getRoute().getDepartureDest() : null,
                booking.hasInboundRouteListing() ? booking.getInboundRouteListing().getRouteListingPk().getRoute().getArrivalDest() : null,
                booking.hasInboundRouteListing() ? booking.getInboundRouteListing().getRouteListingPk().getDepartureDatetime() : null,

                booking.getStartBookingDatetime(),
                booking.getPartySize(),
                booking.getChargedPrice(),
                booking.isPaid(),

                // Check if seat listing matches outbound route listing's planeId, routeId, departureDatetime
                // If yes, add the seatNumber to the outbound list
                // Repeat for inbound route listing
                booking.getSeatListing() != null ?
                booking.getSeatListing().stream()
                        .filter(s -> s.getSeatListingPk().checkSeatBelongsToRouteListing(s, booking.getOutboundRouteListing().getRouteListingPk()))
                        .collect(Collectors.toMap(
                                seatListing -> seatListing.getSeatListingPk().getSeat().getSeatPk().getSeatNumber(),
                                seatListing -> seatListing.getOccupantName() == null ? "UNSPECIFIED" : seatListing.getOccupantName()
                        )) : null,
                booking.hasInboundRouteListing() && booking.getSeatListing() != null ?
                booking.getSeatListing().stream()
                        .filter(s -> s.getSeatListingPk().checkSeatBelongsToRouteListing(s, booking.getInboundRouteListing().getRouteListingPk()))
                        .collect(Collectors.toMap(
                                seatListing -> seatListing.getSeatListingPk().getSeat().getSeatPk().getSeatNumber(),
                                seatListing -> seatListing.getOccupantName() == null ? "UNSPECIFIED" : seatListing.getOccupantName()
                        )) : null
        );
    }

    public Integer getBookingId() {
        return bookingId;
    }

    public void setBookingId(Integer bookingId) {
        this.bookingId = bookingId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOutboundPlaneId() {
        return outboundPlaneId;
    }

    public void setOutboundPlaneId(String outboundPlaneId) {
        this.outboundPlaneId = outboundPlaneId;
    }

    public int getOutboundRouteId() {
        return outboundRouteId;
    }

    public void setOutboundRouteId(int outboundRouteId) { this.outboundRouteId = outboundRouteId; }

    public String getOutboundDepartureDestination() { return outboundDepartureDestination; }

    public void setOutboundDepartureDestination(String outboundDepartureDestination) { this.outboundDepartureDestination = outboundDepartureDestination; }

    public String getOutboundArrivalDestination() { return outboundArrivalDestination; }

    public void setOutboundArrivalDestination(String outboundArrivalDestination) { this.outboundArrivalDestination = outboundArrivalDestination; }

    public LocalDateTime getOutboundDepartureDatetime() {
        return outboundDepartureDatetime;
    }

    public void setOutboundDepartureDatetime(LocalDateTime outboundDepartureDatetime) { this.outboundDepartureDatetime = outboundDepartureDatetime; }

    public String getInboundPlaneId() {
        return inboundPlaneId;
    }

    public void setInboundPlaneId(String inboundPlaneId) {
        this.inboundPlaneId = inboundPlaneId;
    }

    public int getInboundRouteId() {
        return inboundRouteId;
    }

    public void setInboundRouteId(int inboundRouteId) {
        this.inboundRouteId = inboundRouteId;
    }

    public String getInboundDepartureDestination() { return inboundDepartureDestination; }

    public void setInboundDepartureDestination(String inboundDepartureDestination) { this.inboundDepartureDestination = inboundDepartureDestination; }

    public String getInboundArrivalDestination() { return inboundArrivalDestination; }

    public void setInboundArrivalDestination(String inboundArrivalDestination) { this.inboundArrivalDestination = inboundArrivalDestination; }

    public LocalDateTime getInboundDepartureDatetime() {
        return inboundDepartureDatetime;
    }

    public void setInboundDepartureDatetime(LocalDateTime inboundDepartureDatetime) { this.inboundDepartureDatetime = inboundDepartureDatetime; }

    public LocalDateTime getStartBookingDatetime() {
        return startBookingDatetime;
    }

    public void setStartBookingDatetime(LocalDateTime startBookingDatetime) { this.startBookingDatetime = startBookingDatetime; }

    public int getPartySize() {
        return partySize;
    }

    public void setPartySize(int partySize) {
        this.partySize = partySize;
    }

    public double getChargedPrice() {
        return chargedPrice;
    }

    public void setChargedPrice(double chargedPrice) {
        this.chargedPrice = chargedPrice;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }

    public Map<String, String> getOutboundSeatNumbers() {
        return outboundSeatNumbers;
    }

    public void setOutboundSeatNumbers(Map<String, String> outboundSeatNumbers) { this.outboundSeatNumbers = outboundSeatNumbers; }

    public Map<String, String> getInboundSeatNumbers() {
        return inboundSeatNumbers;
    }

    public void setInboundSeatNumbers(Map<String, String> inboundSeatNumbers) { this.inboundSeatNumbers = inboundSeatNumbers; }
}
