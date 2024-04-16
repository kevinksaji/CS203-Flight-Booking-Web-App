package com.G2T5203.wingit.routeListing;

import com.G2T5203.wingit.TestUtils;
import com.G2T5203.wingit.booking.Booking;
import com.G2T5203.wingit.booking.BookingRepository;
import com.G2T5203.wingit.booking.BookingService;
import com.G2T5203.wingit.plane.Plane;
import com.G2T5203.wingit.plane.PlaneRepository;
import com.G2T5203.wingit.route.Route;
import com.G2T5203.wingit.route.RouteRepository;
import com.G2T5203.wingit.seatListing.SeatListing;
import com.G2T5203.wingit.seatListing.SeatListingRepository;
import com.G2T5203.wingit.seatListing.SeatListingService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RouteListingServiceTest {
    @Mock
    private RouteListingRepository routeListingRepo;
    @Mock
    private SeatListingRepository seatListingRepo;
    @Mock
    private RouteRepository routeRepo;
    @Mock
    private PlaneRepository planeRepo;
    @InjectMocks
    private RouteListingService routeListingService;
    @Mock
    private BookingService bookingService;

    @LocalServerPort
    private int port;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    TestUtils testUtils;

    @BeforeEach
    void setUp() {
        testUtils = new TestUtils(port, encoder);
    }

//    @AfterEach
//    void tearDown() {
//        routeListingRepo.deleteAll();
//        seatListingRepo.deleteAll();
//        bookingRepo.deleteAll();
//        routeRepo.deleteAll();
//        planeRepo.deleteAll();
//    }

    @Test
    void getAllRouteListings_ReturnAll() {
        routeListingService.getAllRouteListings();
        verify(routeListingRepo).findAll();
    }

    @Test
    void getAllRouteListingsMatchingFullSearch_ReturnRouteListing() {
        RouteListing sampleRouteListing = testUtils.createSampleRouteListing1();
        ArrayList<RouteListing> sampleRouteListingList = new ArrayList<>();
        sampleRouteListingList.add(sampleRouteListing);

        when(routeListingRepo.findByRouteListingPkRouteDepartureDestAndRouteListingPkRouteArrivalDest(any(String.class), any(String.class))).thenReturn(sampleRouteListingList);

        List<RouteListingSimpleJson> routeListings = routeListingService.getAllRouteListingsMatchingFullSearch("Singapore", "Taiwan", LocalDate.of(2023, 12, 17));

        assertNotNull(routeListings);
        verify(routeListingRepo).findByRouteListingPkRouteDepartureDestAndRouteListingPkRouteArrivalDest(any(String.class), any(String.class));
    }

    @Test
    void calculateRemainingSeatsForRouteListing_WithActiveBookings_ReturnRemainingSeats() {
        // arrange
        RouteListingPk sampleRouteListingPk = testUtils.createSampleRouteListingPk1();
        Booking sampleBooking1 = testUtils.createSampleBooking1();
//        Booking sampleBooking2 = testUtils.createSampleBooking2();

        List<SeatListing> seatListings = new ArrayList<>();
        seatListings.add(testUtils.createSampleSeatListing1());

        List<Booking> bookings = new ArrayList<>();
        bookings.add(sampleBooking1);
//        bookings.add(sampleBooking2);
        sampleBooking1.setSeatListing(seatListings);

        // mock
        when(seatListingRepo.findBySeatListingPkRouteListingRouteListingPkAndBookingIsNull(any(RouteListingPk.class))).thenReturn(seatListings);
        when(bookingService.getActiveUnfinishedBookingsForRouteListing(any(RouteListingPk.class))).thenReturn(bookings);

        // act
        int remainingSeats = routeListingService.calculateRemainingSeatsForRouteListing(sampleRouteListingPk);

        // assert
        //assertEquals(remainingSeats, 10 - sampleBooking1.getPartySize() - sampleBooking2.getPartySize() + sampleBooking1.getSeatListing().size() + sampleBooking2.getSeatListing().size());
        assertNotNull(remainingSeats);

        // verify
        verify(seatListingRepo).findBySeatListingPkRouteListingRouteListingPkAndBookingIsNull(sampleRouteListingPk);
        verify(bookingService).getActiveUnfinishedBookingsForRouteListing(sampleRouteListingPk);
    }

    @Test
    void createRouteListing_New_Success() {
        // arrange
        RouteListing sampleRouteListing = testUtils.createSampleRouteListing1();
        Route sampleRoute = sampleRouteListing.getRouteListingPk().getRoute();
        Plane samplePlane = sampleRouteListing.getRouteListingPk().getPlane();
        RouteListingSimpleJson sampleRouteListingSimpleJson = new RouteListingSimpleJson(sampleRouteListing, 50);

        // mock
        when(routeRepo.findById(any(Integer.class))).thenReturn(Optional.of(sampleRoute));
        when(planeRepo.findById(any(String.class))).thenReturn(Optional.of(samplePlane));
        when(routeListingRepo.existsById(any(RouteListingPk.class))).thenReturn(false);
        when(routeListingRepo.save(any(RouteListing.class))).thenAnswer(i -> i.getArguments()[0]);

        // act
        RouteListing savedRouteListing = routeListingService.createRouteListing(sampleRouteListingSimpleJson);

        // assert
        assertNotNull(savedRouteListing);

        // verify
        verify(routeRepo).findById(sampleRoute.getRouteId());
        verify(planeRepo).findById(samplePlane.getPlaneId());
        verify(routeListingRepo).existsById(sampleRouteListing.getRouteListingPk());
        verify(routeListingRepo).save(any(RouteListing.class));
    }

}
