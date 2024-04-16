package com.G2T5203.wingit.adminUtils;

import com.G2T5203.wingit.booking.Booking;
import com.G2T5203.wingit.booking.BookingRepository;
import com.G2T5203.wingit.booking.BookingService;
import com.G2T5203.wingit.plane.Plane;
import com.G2T5203.wingit.plane.PlaneRepository;
import com.G2T5203.wingit.route.Route;
import com.G2T5203.wingit.route.RouteRepository;
import com.G2T5203.wingit.routeListing.RouteListing;
import com.G2T5203.wingit.routeListing.RouteListingPk;
import com.G2T5203.wingit.routeListing.RouteListingRepository;
import com.G2T5203.wingit.seat.*;
import com.G2T5203.wingit.seatListing.*;
import com.G2T5203.wingit.user.UserRepository;
import com.G2T5203.wingit.user.WingitUser;
import com.G2T5203.wingit.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

// Class is used only in DEV profile to pre-populate it with data for testing purposes.

public class DatabaseInitializer {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);
    private static void Log(String msg) { logger.info(msg); }
    private static List<WingitUser> sampleUserList = new ArrayList<>(3);
    public static List<WingitUser> getSampleUserList() { return sampleUserList; }

    private static void initialiseSampleUsers(UserRepository repo, BCryptPasswordEncoder encoder) {
        sampleUserList.clear();
        sampleUserList.add(repo.save(new WingitUser(
                "brandonDaddy",
                encoder.encode("goodpassword"),
                "ROLE_USER",
                "Brandon",
                "Choy",
                LocalDate.parse("2001-12-04"),
                "brandon.choy.2037@scis.smu.edu.sg",
                "+65 8746 3847",
                "Mr")));
        sampleUserList.add(repo.save(new WingitUser(
                "DaddyChoy",
                encoder.encode("password"),
                "ROLE_USER",
                "Jared",
                "Hong",
                LocalDate.parse("1996-10-03"),
                "jared.hong.2034@scis.smu.edu.sg",
                "+65 8455 0750",
                "Mrs")));
        sampleUserList.add(repo.save(new WingitUser(
                "richman",
                encoder.encode("iamrich"),
                "ROLE_USER",
                "Richmond",
                "Musk",
                LocalDate.parse("1960-04-02"),
                "rich@richmond.com",
                "+65 8888 8888",
                "Mr")));
    }
    private static void initialiseSamplePlanes(List<Plane> list, PlaneRepository repo) {
        list.add(repo.save(new Plane( "SQ123", 42, "B777")));
        list.add(repo.save(new Plane( "SQ888", 30, "A350")));
        list.add(repo.save(new Plane( "SQ364", 42, "B777")));
        list.add(repo.save(new Plane( "SQ39", 30, "A350")));
        list.add(repo.save(new Plane( "SQ28", 42, "B777")));
        list.add(repo.save(new Plane( "SQ493", 30, "A350")));
        list.add(repo.save(new Plane( "SQ789", 42, "B777")));
        list.add(repo.save(new Plane( "SQ394", 30, "A350")));
        list.add(repo.save(new Plane( "SQ347", 42, "B777")));
        list.add(repo.save(new Plane( "SQ977", 30, "A350")));

        list.add(repo.save(new Plane( "SQ124", 42, "B777")));
        list.add(repo.save(new Plane( "SQ808", 30, "A350")));
        list.add(repo.save(new Plane( "SQ563", 42, "B777")));
        list.add(repo.save(new Plane( "SQ381", 30, "A350")));
        list.add(repo.save(new Plane( "SQ293", 42, "B777")));
        list.add(repo.save(new Plane( "SQ593", 30, "A350")));
        list.add(repo.save(new Plane( "SQ987", 42, "B777")));
        list.add(repo.save(new Plane( "SQ333", 30, "A350")));
        list.add(repo.save(new Plane( "SQ352", 42, "B777")));
        list.add(repo.save(new Plane( "SQ779", 30, "A350")));


        list.add(repo.save(new Plane( "SQ21", 42, "B777")));
        list.add(repo.save(new Plane( "SQ800", 30, "A350")));
        list.add(repo.save(new Plane( "SQ362", 42, "B777")));
        list.add(repo.save(new Plane( "SQ390", 30, "A350")));
        list.add(repo.save(new Plane( "SQ280", 42, "B777")));
        list.add(repo.save(new Plane( "SQ492", 30, "A350")));
        list.add(repo.save(new Plane( "SQ787", 42, "B777")));
        list.add(repo.save(new Plane( "SQ360", 30, "A350")));
        list.add(repo.save(new Plane( "SQ340", 42, "B777")));
        list.add(repo.save(new Plane( "SQ979", 30, "A350")));

        list.add(repo.save(new Plane( "SQ121", 42, "B777")));
        list.add(repo.save(new Plane( "SQ87", 30, "A350")));
        list.add(repo.save(new Plane( "SQ568", 42, "B777")));
        list.add(repo.save(new Plane( "SQ380", 30, "A350")));
        list.add(repo.save(new Plane( "SQ290", 42, "B777")));
        list.add(repo.save(new Plane( "SQ595", 30, "A350")));
        list.add(repo.save(new Plane( "SQ989", 42, "B777")));
        list.add(repo.save(new Plane( "SQ343", 30, "A350")));
        list.add(repo.save(new Plane( "SQ350", 42, "B777")));
        list.add(repo.save(new Plane( "SQ777", 30, "A350")));

        list.add(repo.save(new Plane("SQ288", 30, "A350")));
    }
    private static void initialiseSampleSeats(List<Plane> planeList, SeatService seatService) {
        for (Plane plane : planeList) {
            seatService.createSeatsForNewPlane(plane);
        }
    }
    private static void initialiseSampleRoutes(List<Route> list, RouteRepository repo, boolean isProduction) {
        list.add(repo.save(new Route(
                "Singapore",
                "Taiwan",
                Duration.ofHours(4).plusMinutes(45) )));
        list.add(repo.save(new Route(
                "Singapore",
                "Japan",
                Duration.ofHours(7).plusMinutes(10) )));
        list.add(repo.save(new Route(
                "Singapore",
                "China",
                Duration.ofHours(6).plusMinutes(10) )));
        list.add(repo.save(new Route(
                "Singapore",
                "India",
                Duration.ofHours(5).plusMinutes(35) )));

        list.add(repo.save(new Route(
                "Taiwan",
                "Singapore",
                Duration.ofHours(4).plusMinutes(50) )));

        if (isProduction) return;

        list.add(repo.save(new Route(
                "Taiwan",
                "Japan",
                Duration.ofHours(2).plusMinutes(40) )));
        list.add(repo.save(new Route(
                "Taiwan",
                "China",
                Duration.ofHours(1).plusMinutes(50) )));
        list.add(repo.save(new Route(
                "Taiwan",
                "India",
                Duration.ofHours(12).plusMinutes(25) )));

        list.add(repo.save(new Route(
                "Japan",
                "Singapore",
                Duration.ofHours(7).plusMinutes(15) )));
        list.add(repo.save(new Route(
                "Japan",
                "Taiwan",
                Duration.ofHours(2).plusMinutes(35) )));
        list.add(repo.save(new Route(
                "Japan",
                "China",
                Duration.ofHours(3).plusMinutes(30) )));
        list.add(repo.save(new Route(
                "Japan",
                "India",
                Duration.ofHours(10).plusMinutes(10) )));

        list.add(repo.save(new Route(
                "China",
                "Singapore",
                Duration.ofHours(6).plusMinutes(15) )));
        list.add(repo.save(new Route(
                "China",
                "Taiwan",
                Duration.ofHours(1).plusMinutes(40) )));
        list.add(repo.save(new Route(
                "China",
                "Japan",
                Duration.ofHours(3).plusMinutes(40) )));
        list.add(repo.save(new Route(
                "China",
                "India",
                Duration.ofHours(12).plusMinutes(55) )));

        list.add(repo.save(new Route(
                "India",
                "Singapore",
                Duration.ofHours(5).plusMinutes(40) )));
        list.add(repo.save(new Route(
                "India",
                "Taiwan",
                Duration.ofHours(12).plusMinutes(35) )));
        list.add(repo.save(new Route(
                "India",
                "Japan",
                Duration.ofHours(10).plusMinutes(15) )));
        list.add(repo.save(new Route(
                "India",
                "China",
                Duration.ofHours(12).plusMinutes(45) )));
    }
    private static void initialiseSampleRouteListings(List<RouteListing> list, RouteListingRepository repo, List<Plane> planeList, List<Route> routeList, boolean isProduction) {
        for (int year = 2023; year <= 2023; year++) {
            for (int month = 12; month <= 12; month++) {
                int daysInMonth;
                if (month == 2) daysInMonth = 28;
                else if (month == 4 || month == 6 || month == 9 || month == 11) daysInMonth = 30;
                else daysInMonth = 31;
                for (int day = 1; day <= daysInMonth; day++) {
                    // NOTE: We have equal double number of planes and routes. Two planes serves each route for sample database.
                    // NOTE: 40 planes, 20 routes. Each Route has two planes flying that route per day.
                    if (isProduction) {
                        // In production we skip everything that is not a multiple of 5 and also everything that is not 2023 Dec.
                        if (year != 2023 || month != 12) continue;
                        if (day % 5 != 0) continue;
                    }

                    for (int planeOffset = 0; planeOffset < 2; planeOffset++) {
                        for (int i = 0; i < routeList.size(); i++) {
                            int hour = i % 2 == 0 ? 7 : 13;
                            hour += planeOffset;
                            int minute = 15 * (i % 4);
                            minute += planeOffset * 3;
                            String datetimeStr = String.format("%d-%02d-%02d %02d:%02d:00", year, month, day, hour, minute);

                            double price = 500.0;
                            Route route = routeList.get(i);
                            price += route.getFlightDuration().toMinutes() * 0.374 + 7.34 * minute;
                            list.add(repo.save(new RouteListing(
                                    new RouteListingPk(
                                            planeList.get(i + (planeOffset * 20)),
                                            route,
                                            DateUtils.handledParseDateTime(datetimeStr)),
                                    price
                            )));
                        }
                    }
                }
            }
        }
    }
    private static void initialiseSampleSeatListings(SeatListingService seatListingService, List<RouteListing> routeListingList) {
        long counter = 0;
        for (RouteListing routeListing : routeListingList) {
            seatListingService.createSeatListingsForNewRouteListing(routeListing);
            counter++;
            if (counter % 100 == 0 || counter == routeListingList.size())
                Log(String.format("[SeatListing progress... %d/%d", counter, routeListingList.size()));
        }
    }

    private static void initialiseSampleBookings(List<Booking> list, BookingRepository repo, WingitUser richUser, List<RouteListing> routeListingList, SeatListingService seatListingService, BookingService bookingService) {
        // Go through each and every routeListing
        // Book maybe half of the tickets... So seeded Randomization of what tickets to book.
        // Book it all under this one mega user

        int counter = 0;

        final long RANDOM_SEED = 777L;
        Random outerRandom = new Random(RANDOM_SEED);
        for (RouteListing routeListing : routeListingList) {
            final int NUM_BOOKINGS_PER_ROUTELISTING = 5;
            for (int k = 0 ; k < NUM_BOOKINGS_PER_ROUTELISTING; k++) {
                Random random = new Random(RANDOM_SEED + k);
                int partySize = outerRandom.nextInt(5) + 1;

                Booking newBooking = repo.save(new Booking(
                        richUser,
                        routeListing,
                        null,
                        LocalDateTime.now().minusSeconds(1L),
                        partySize,
                        -1,
                        false));
                list.add(newBooking);

                List<PrivacySeatListingSimpleJson> seatListings = seatListingService.getAllSeatListingsInRouteListing(
                        routeListing.getRouteListingPk().getPlane().getPlaneId(),
                        routeListing.getRouteListingPk().getRoute().getRouteId(),
                        routeListing.getRouteListingPk().getDepartureDatetime());

                for (int i = 0; i < partySize; i++) {
                    int seatIndex = random.nextInt(seatListings.size());
                    PrivacySeatListingSimpleJson seatChosen = seatListings.get(seatIndex);
                    while (seatChosen.getIsBooked()) {
                        seatIndex = random.nextInt(seatListings.size());
                        seatChosen = seatListings.get(seatIndex);
                    }

                    seatListingService.reserveSeatListing(
                            seatChosen.getPlaneId(),
                            seatChosen.getRouteId(),
                            seatChosen.getDepartureDatetime(),
                            seatChosen.getSeatNumber(),
                            newBooking.getBookingId());
                    SeatListing updatedSeatListing = seatListingService.setOccupantForSeatListing(
                            seatChosen.getPlaneId(),
                            seatChosen.getRouteId(),
                            seatChosen.getDepartureDatetime(),
                            seatChosen.getSeatNumber(),
                            newBooking.getBookingId(),
                            richUser.getFirstName() + "_" + k + "_" + i);
                    seatListings.set(seatIndex, new PrivacySeatListingSimpleJson(updatedSeatListing));
                }

                bookingService.calculateAndSaveChargedPrice(newBooking.getBookingId());
                bookingService.markBookingAsPaid(newBooking.getBookingId());
            }

            counter++;
            if (counter % 25 == 0 || counter == routeListingList.size())
                Log(String.format("[Bookings progress... %d/%d", counter, routeListingList.size()));
        }
    }

    private static void createAlmostFullFlight(
            BookingRepository bookingRepo,
            RouteListingRepository routeListingRepository,
            PlaneRepository planeRepo,
            RouteRepository routeRepository,
            SeatListingService seatListingService,
            WingitUser richUser,
            BookingService bookingService) {
        Optional<Plane> optionalPlane = planeRepo.findById("SQ288");
        List<Route> matchingRoutes = routeRepository.findAllByDepartureDestAndArrivalDest("Taiwan", "Singapore");
        if (optionalPlane.isEmpty() || matchingRoutes.isEmpty()) {
            Log("ERROR: Unable to create almost full flight");
            return;
        }

        Plane plane = optionalPlane.get();
        Route route = matchingRoutes.get(0);

        String datetimeStr = String.format("%d-%02d-%02d %02d:%02d:00", 2023, 12, 17, 13, 10);
        RouteListing newRouteListing = routeListingRepository.save(new RouteListing(
                new RouteListingPk(plane, route, DateUtils.handledParseDateTime(datetimeStr)),
                777.77));
        seatListingService.createSeatListingsForNewRouteListing(newRouteListing);



        // Create 27 bookings to leave 3 seatListings left.
        final int PAX_SIZE = 27;
        for (int iBooking = 0; (iBooking * 10) < PAX_SIZE; iBooking++) { // 0, 10, 20,
            Booking newBooking = bookingRepo.save(new Booking(
                    richUser,
                    newRouteListing,
                    null,
                    LocalDateTime.now().minusSeconds(1L),
                    Math.min(10, PAX_SIZE - (iBooking * 10)), // 10, 10, 7
                    -1,
                    false));
            List<PrivacySeatListingSimpleJson> seatListings = seatListingService.getAllSeatListingsInRouteListing(
                    newRouteListing.getRouteListingPk().getPlane().getPlaneId(),
                    newRouteListing.getRouteListingPk().getRoute().getRouteId(),
                    newRouteListing.getRouteListingPk().getDepartureDatetime());

            for (int i = (iBooking * 10); // 0, 10, 20
                 i < Math.min(PAX_SIZE, ((iBooking + 1) * 10)); // 10, 20, 27
                 i++) {
                PrivacySeatListingSimpleJson seatChosen = seatListings.get(i);
                seatListingService.reserveSeatListing(
                        seatChosen.getPlaneId(),
                        seatChosen.getRouteId(),
                        seatChosen.getDepartureDatetime(),
                        seatChosen.getSeatNumber(),
                        newBooking.getBookingId());
                seatListingService.setOccupantForSeatListing(
                        seatChosen.getPlaneId(),
                        seatChosen.getRouteId(),
                        seatChosen.getDepartureDatetime(),
                        seatChosen.getSeatNumber(),
                        newBooking.getBookingId(),
                        richUser.getFirstName() + "_" + i);
            }

            bookingService.calculateAndSaveChargedPrice(newBooking.getBookingId());
            bookingService.markBookingAsPaid(newBooking.getBookingId());
        }
        Log("[Created almost full booking!]");
    }

    public static void initNonAdminUsersData(ApplicationContext context) {
        // Get encoder
        BCryptPasswordEncoder encoder = context.getBean(BCryptPasswordEncoder.class);

        // Initialise WingitUsers
        UserRepository userRepository = context.getBean(UserRepository.class);
        initialiseSampleUsers(userRepository, encoder);
//        for (WingitUser wingitUser : wingitUserList) { Log("[Add WingitUser]: " + wingitUser); }
        Log("[Added sample WingitUsers]");
    }
    public static void initPlanesAndRoutesData(ApplicationContext context, boolean isProduction) {
        // Initialise Planes
        PlaneRepository planeRepository = context.getBean(PlaneRepository.class);
        List<Plane> planeList = new ArrayList<>();
        initialiseSamplePlanes(planeList, planeRepository);
//        for (Plane plane : planeList) { Log("[Add Plane]: " + plane); }
        Log("[Added sample Planes]");


        // Initialise Seats
        SeatRepository seatRepository = context.getBean(SeatRepository.class);
        SeatService seatService = context.getBean(SeatService.class);
        initialiseSampleSeats(planeList, seatService);
        Log("[Added sample Seats]");


        // Initialise Routes
        RouteRepository routeRepository = context.getBean(RouteRepository.class);
        List<Route> routeList = new ArrayList<>();
        initialiseSampleRoutes(routeList, routeRepository, isProduction);
//        for (Route route : routeList) { Log("[Add Route]: " + route); }
        Log("[Added sample Routes]");


        // Initialise RouteListings
        RouteListingRepository routeListingRepository = context.getBean(RouteListingRepository.class);
        List<RouteListing> routeListingList = new ArrayList<>();
        initialiseSampleRouteListings(routeListingList, routeListingRepository, planeList, routeList, isProduction);
//        for (RouteListing routeListing : routeListingList) { Log("[Add RouteListing]: " + routeListing); }
        Log("[Added sample RouteListings]");


        // Initialise SeatListings
        SeatListingService seatListingService = context.getBean(SeatListingService.class);
        initialiseSampleSeatListings(seatListingService, routeListingList);
        Log("[Added sample SeatListing]");

        // Initialise Bookings
        BookingRepository bookingRepository = context.getBean(BookingRepository.class);
        BookingService bookingService = context.getBean(BookingService.class);
        List<Booking> bookingList = new ArrayList<>();
        UserRepository userRepository = context.getBean(UserRepository.class);
        Optional<WingitUser> optionalRichUser = userRepository.findByUsername("richman");
        if (optionalRichUser.isEmpty()) {
            Log("ERROR: Unable to locate rich user for dummy data.");
            return;
        }
        WingitUser richUser = optionalRichUser.get();
        initialiseSampleBookings(bookingList, bookingRepository, richUser, routeListingList, seatListingService, bookingService);
//        for (Booking booking : bookingList) { Log("[Add Booking]: " + booking); }
        Log("[Added sample Bookings]");

        createAlmostFullFlight(
                bookingRepository,
                routeListingRepository,
                planeRepository,
                routeRepository,
                seatListingService,
                richUser,
                bookingService);

        Log("[Finished Initialising Sample Database Data]");
    }
}
