package com.G2T5203.wingit;

import com.G2T5203.wingit.booking.Booking;
import com.G2T5203.wingit.booking.BookingSimpleJson;
import com.G2T5203.wingit.plane.Plane;
import com.G2T5203.wingit.route.Route;
import com.G2T5203.wingit.routeListing.RouteListing;
import com.G2T5203.wingit.routeListing.RouteListingPk;
import com.G2T5203.wingit.seat.Seat;
import com.G2T5203.wingit.seat.SeatPk;
import com.G2T5203.wingit.seatListing.PrivacySeatListingSimpleJson;
import com.G2T5203.wingit.seatListing.SeatListing;
import com.G2T5203.wingit.seatListing.SeatListingPk;
import com.G2T5203.wingit.seatListing.SeatListingSimpleJson;
import com.G2T5203.wingit.user.WingitUser;
import com.G2T5203.wingit.routeListing.RouteListingSimpleJson;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class TestUtils {
    private final int port;
    private final BCryptPasswordEncoder encoder;

    public final String ADMIN_USERNAME = "admin";
    public final String ADMIN_PASSWORD = "pass";
    public final String SAMPLE_USERNAME_1 = "brandonDaddy";
    public final String SAMPLE_PASSWORD_1 = "goodpassword";
    public final String SAMPLE_USERNAME_2 = "DaddyChoy";
    public final String SAMPLE_PASSWORD_2 = "password";

    public TestUtils(int port, BCryptPasswordEncoder encoder) {
        this.port = port;
        this.encoder = encoder;
    }

    public URI constructUri(String path) throws URISyntaxException {
        String baseUrl = "http://localhost:";
        return new URI(baseUrl + port + "/" + path);
    }
    // Helper functions
    public WingitUser createSampleUser1() { return createSampleUser1(true); }
    public WingitUser createSampleUser1(boolean encodePassword) {
        return new WingitUser(
                SAMPLE_USERNAME_1,
                encodePassword ? encoder.encode(SAMPLE_PASSWORD_1) : SAMPLE_PASSWORD_1,
                "ROLE_USER",
                "Brandon",
                "Choy",
                LocalDate.parse("2001-12-04"),
                "brandon.choy.2037@scis.smu.edu.sg",
                "+65 8746 3847",
                "Mr");
    }
    public WingitUser createSampleUser2() { return createSampleUser2(true); }
    public WingitUser createSampleUser2(boolean encodePassword) {
        return new WingitUser(
                SAMPLE_USERNAME_2,
                encodePassword ? encoder.encode(SAMPLE_PASSWORD_2) : SAMPLE_PASSWORD_2,
                "ROLE_USER",
                "Jared",
                "Hong",
                LocalDate.parse("1996-10-03"),
                "jared.hong.2034@scis.smu.edu.sg",
                "+65 8455 0750",
                "Mrs");
    }
    public WingitUser createAdminUser() { return createAdminUser(true); }
    public WingitUser createAdminUser(boolean encodePassword) {
        return new WingitUser(
                ADMIN_USERNAME,
                encodePassword ? encoder.encode(ADMIN_PASSWORD) : ADMIN_PASSWORD,
                "ROLE_ADMIN",
                "admin",
                "admin",
                LocalDate.parse("2000-01-01"),
                "admin@admin.com",
                "+65 6475 3846",
                "Master");
    }

    public Plane createSamplePlane1() {
        return new Plane("SQ123", 60, "B777");
    }
    public Plane createSamplePlane2() {
        return new Plane("SQ456", 120, "A350");
    }

    public Route createSampleRoute1() {
        return new Route(
                -1, // NOTE: It can be overridden as this is generated value.
                "Singapore",
                "Taiwan",
                Duration.ofHours(5).plusMinutes(20));
    }
    public Route createSampleRoute2() {
        return new Route(
                -1, // NOTE: It can be overridden as this is generated value.
                "Taiwan",
                "Singapore",
                Duration.ofHours(7).plusMinutes(10));
    }

    // Create sample RouteListingPks
    public RouteListingPk createSampleRouteListingPk1() {
        Plane samplePlane1 = createSamplePlane1();
        Route sampleRoute1 = createSampleRoute1();
        LocalDateTime sampleLocalDatetime1 = LocalDateTime.parse("2023-12-01T00:00:00");
        return new RouteListingPk(samplePlane1, sampleRoute1, sampleLocalDatetime1);
    }

    public RouteListingPk createSampleRouteListingPk2() {
        Plane samplePlane2 = createSamplePlane2();
        Route sampleRoute2 = createSampleRoute2();
        LocalDateTime sampleLocalDatetime2 = LocalDateTime.parse("2023-12-02T00:00:00");
        return new RouteListingPk(samplePlane2, sampleRoute2, sampleLocalDatetime2);
    }

    // Create sample RouteListings
    public RouteListing createSampleRouteListing1() {
        RouteListingPk sampleRouteListingPk1 = createSampleRouteListingPk1();
        double sampleBasePrice1 = 100.0;
        return new RouteListing(sampleRouteListingPk1, sampleBasePrice1);
    }

    public RouteListing createSampleRouteListing2() {
        RouteListingPk sampleRouteListingPk2 = createSampleRouteListingPk2();
        double sampleBasePrice2 = 200.0;
        return new RouteListing(sampleRouteListingPk2, sampleBasePrice2);
    }

    // Create sample SeatPk
    public SeatPk createSampleSeatPk1() {
        Plane samplePlane1 = createSamplePlane1();
        String sampleSeatNumber1 = "A1";
        return new SeatPk(samplePlane1, sampleSeatNumber1);
    }

    public SeatPk createSampleSeatPk2() {
        Plane samplePlane2 = createSamplePlane2();
        String sampleSeatNumber1 = "A1";
        return new SeatPk(samplePlane2, sampleSeatNumber1);
    }

    // Create sample Seat
    public Seat createSampleSeat1() {
        SeatPk sampleSeatPk1 = createSampleSeatPk1();
        String sampleSeatClass = "Economy";
        double samplePriceFactor = 1.0;
        return new Seat(sampleSeatPk1, sampleSeatClass, samplePriceFactor);
    }

    public Seat createSampleSeat2() {
        SeatPk sampleSeatPk2 = createSampleSeatPk2();
        String sampleSeatClass = "Economy";
        double samplePriceFactor = 1.0;
        return new Seat(sampleSeatPk2, sampleSeatClass, samplePriceFactor);
    }

    // Create sample Booking
    public Booking createSampleBooking1() {
        int sampleBookingId1 = 1;
        WingitUser sampleUser1 = createSampleUser1();
        RouteListing sampleOutboundRouteListing = createSampleRouteListing1();
        RouteListing sampleInboundRouteListing = null;
        LocalDateTime sampleStartBookingDatetime = LocalDateTime.now();
        int samplePartySize = 1;
        double sampleChargedPrice = 100;
        boolean isPaid = false;
        return new Booking(sampleBookingId1, sampleUser1, sampleOutboundRouteListing,
                sampleInboundRouteListing, sampleStartBookingDatetime, samplePartySize,
                sampleChargedPrice, isPaid);
    }

    public Booking createSampleBooking2() {
        int sampleBookingId2 = 2;
        WingitUser sampleUser1 = createSampleUser1();
        RouteListing sampleOutboundRouteListing = createSampleRouteListing1();
        RouteListing sampleInboundRouteListing = createSampleRouteListing2();
        LocalDateTime sampleStartBookingDatetime = LocalDateTime.now();
        int samplePartySize = 1;
        double sampleChargedPrice = 100;
        boolean isPaid = false;
        return new Booking(sampleBookingId2, sampleUser1, sampleOutboundRouteListing,
                sampleInboundRouteListing, sampleStartBookingDatetime, samplePartySize,
                sampleChargedPrice, isPaid);
    }

    public BookingSimpleJson createSampleBookingSimpleJson1() {
        return new BookingSimpleJson(createSampleBooking1());
    }

    // Create sample SeatListingPk
    public SeatListingPk createSampleSeatListingPk1(){
        RouteListing sampleRouteListing = createSampleRouteListing1();
        Seat sampleSeat = createSampleSeat1();
        return new SeatListingPk(sampleRouteListing, sampleSeat);
    }

    public SeatListingPk createSampleSeatListingPk2(){
        RouteListing sampleRouteListing = createSampleRouteListing2();
        Seat sampleSeat = createSampleSeat1();
        return new SeatListingPk(sampleRouteListing, sampleSeat);
    }

    // Create sample SeatListing
    public SeatListing createSampleSeatListing1() {
        SeatListingPk sampleSeatListingPk1 = createSampleSeatListingPk1();
        Booking sampleBooking1 = createSampleBooking1();
        String sampleOccupantName = "Amy";
        return new SeatListing(sampleSeatListingPk1, sampleBooking1, sampleOccupantName);
    }

    public SeatListing createSampleSeatListing2() {
        SeatListingPk sampleSeatListingPk2 = createSampleSeatListingPk2();
        Booking sampleBooking2 = createSampleBooking2();
        String sampleOccupantName = "Amy";
        return new SeatListing(sampleSeatListingPk2, sampleBooking2, sampleOccupantName);
    }

    // Create sample PrivacySeatListingSimpleJson
    public PrivacySeatListingSimpleJson createPrivacySeatListingSimpleJson() {
        SeatListing sampleSeatListing = createSampleSeatListing1();
        return new PrivacySeatListingSimpleJson(sampleSeatListing);
    }

    // Create sample SeatListingSimpleJson
    public SeatListingSimpleJson createSeatListingSimpleJson() {
        SeatListing sampleSeatListing = createSampleSeatListing1();
        return new SeatListingSimpleJson(sampleSeatListing);
    }

    public RouteListingSimpleJson createSampleRouteListingSimpleJson(Route route, Plane plane) {
        return new RouteListingSimpleJson(
                route.getRouteId(),
                plane.getPlaneId(),
                LocalDateTime.now(),
                Duration.ofHours(3),
                100.0,
                5
        );
    }
}
