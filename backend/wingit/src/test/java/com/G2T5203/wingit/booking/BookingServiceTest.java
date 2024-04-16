package com.G2T5203.wingit.booking;

import com.G2T5203.wingit.TestUtils;
import com.G2T5203.wingit.plane.Plane;
import com.G2T5203.wingit.plane.PlaneRepository;
import com.G2T5203.wingit.route.Route;
import com.G2T5203.wingit.route.RouteRepository;
import com.G2T5203.wingit.routeListing.*;
import com.G2T5203.wingit.seatListing.SeatListing;
import com.G2T5203.wingit.seatListing.SeatListingRepository;
import com.G2T5203.wingit.seatListing.SeatListingService;
import com.G2T5203.wingit.user.UserRepository;
import com.G2T5203.wingit.user.WingitUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.swing.text.html.Option;
import java.awt.print.Book;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    @Mock
    private RouteListingRepository routeListingRepo;
    @Mock
    private SeatListingRepository seatListingRepo;
    @Mock
    private BookingRepository bookingRepo;
    @Mock
    private RouteRepository routeRepo;
    @Mock
    private PlaneRepository planeRepo;
    @Mock
    private UserRepository userRepo;
    @Mock
    private RouteListingService routeListingService;
    @Mock
    private SeatListingService seatListingService;
    @InjectMocks
    private BookingService bookingService;

    @LocalServerPort
    private int port;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    TestUtils testUtils;

    @BeforeEach
    void setUp() {
        testUtils = new TestUtils(port, encoder);
    }

    @Test
    void getBookingUserUsername_Success_Return() {
        // arrange
        Booking sampleBooking = testUtils.createSampleBooking1();

        // mock
        when(bookingRepo.findById(any(Integer.class))).thenReturn(Optional.of(sampleBooking));

        // act
        String username = bookingService.getBookingUserUsername(sampleBooking.getBookingId());

        // assert
        assertEquals(sampleBooking.getWingitUser().getUsername(), username);

        // verify
        verify(bookingRepo).findById(sampleBooking.getBookingId());
    }

    @Test
    void getAllBookingsByUser_Success_Return() {
        // arrange
        WingitUser sampleUser = testUtils.createSampleUser1();
        Booking sampleBooking1 = testUtils.createSampleBooking1();
        Booking sampleBooking2 = testUtils.createSampleBooking2();
        List<Booking> sampleBookings = new ArrayList<>();
        sampleBookings.add(sampleBooking1);
        sampleBookings.add(sampleBooking2);

        // mock
        when(bookingRepo.findAllByWingitUserUsername(any(String.class))).thenReturn(sampleBookings);

        // act
        List<BookingSimpleJson> bookings = bookingService.getAllBookingsByUser(sampleUser.getUsername());

        // assert
        assertEquals(2, bookings.size());

        // verify
        verify(bookingRepo).findAllByWingitUserUsername(sampleUser.getUsername());
    }

    @Test
    void calculateRemainingSeatsForRouteListing_Success_Return() {
        // arrange
        RouteListingPk sampleRouteListingPk = testUtils.createSampleRouteListingPk1();
        SeatListing sampleSeatListing1 = testUtils.createSampleSeatListing1();
        SeatListing sampleSeatListing2 = testUtils.createSampleSeatListing2();
        List<SeatListing> sampleSeatListings = new ArrayList<>();
        sampleSeatListings.add(sampleSeatListing1);
        sampleSeatListings.add(sampleSeatListing2);

        // mock
        when(seatListingRepo.findBySeatListingPkRouteListingRouteListingPkAndBookingIsNull(any(RouteListingPk.class))).thenReturn(sampleSeatListings);

        // act
        int remainingSeats = bookingService.calculateRemainingSeatsForRouteListing(sampleRouteListingPk);

        // assert
        assertEquals(2, remainingSeats);

        // verify
        verify(seatListingRepo).findBySeatListingPkRouteListingRouteListingPkAndBookingIsNull(sampleRouteListingPk);
    }

    @Test
    void createBooking_Success_Return() {
        // arrange
        WingitUser sampleUser = testUtils.createSampleUser1();
        Plane samplePlane = testUtils.createSamplePlane1();
        Route sampleRoute = testUtils.createSampleRoute1();
        RouteListing sampleRouteListing = testUtils.createSampleRouteListing1();
        Booking sampleBooking = testUtils.createSampleBooking1();
        BookingSimpleJson sampleBookingSimpleJson = testUtils.createSampleBookingSimpleJson1();

        SeatListing sampleSeatListing1 = testUtils.createSampleSeatListing1();
        SeatListing sampleSeatListing2 = testUtils.createSampleSeatListing2();
        List<SeatListing> sampleSeatListings = new ArrayList<>();
        sampleSeatListings.add(sampleSeatListing1);
        sampleSeatListings.add(sampleSeatListing2);

        // mock
        when(userRepo.findByUsername(any(String.class))).thenReturn(Optional.of(sampleUser));
        when(planeRepo.findById(any(String.class))).thenReturn(Optional.of(samplePlane));
        when(routeRepo.findById(any(Integer.class))).thenReturn(Optional.of(sampleRoute));
        when(routeListingRepo.findById(any(RouteListingPk.class))).thenReturn(Optional.of(sampleRouteListing));

        when(seatListingRepo.findBySeatListingPkRouteListingRouteListingPkAndBookingIsNull(any(RouteListingPk.class))).thenReturn(sampleSeatListings);

        when(bookingRepo.save(any(Booking.class))).thenAnswer((i) -> {
            // hardcode mocking the save function, because bookingId is generated
            Booking newBooking = (Booking) i.getArguments()[0];
            newBooking.setBookingId(1);
            return newBooking;
        });

        // act
        BookingSimpleJson createdBookingSimpleJson = bookingService.createBooking(sampleBookingSimpleJson);

        // assert
        assertEquals(sampleBookingSimpleJson.getBookingId(), createdBookingSimpleJson.getBookingId());

        // verify
        verify(userRepo).findByUsername(sampleUser.getUsername());
        verify(planeRepo).findById(samplePlane.getPlaneId());
        verify(routeRepo).findById(sampleRoute.getRouteId());
        verify(routeListingRepo).findById(any(RouteListingPk.class));
        verify(bookingRepo).save(any(Booking.class));
    }

    @Test
    void deleteBookingById_Success() {
        // arrange
        Booking sampleBooking = testUtils.createSampleBooking1();

        // mock
        when(bookingRepo.existsById(any(Integer.class))).thenReturn(true);

        // act
        bookingService.deleteBookingById(sampleBooking.getBookingId());

        // verify
        verify(bookingRepo).existsById(1);
        verify(bookingRepo).deleteById(1);
    }

//    @Test
//    void forceDeleteBooking_Success() {
//        // arrange
//        RouteListingPk sampleRouteListingPk = testUtils.createSampleRouteListingPk1();
//        SeatListing sampleSeatListing = testUtils.createSampleSeatListing1();
//        Booking sampleBooking = testUtils.createSampleBooking1();
//
//        // mock
//        when(seatListingService.cancelSeatListingBooking(any(String.class),
//                any(Integer.class),
//                any(LocalDateTime.class),
//                any(String.class))).thenReturn(null);
//
//        // act
//        //bookingService.forceDeleteBooking(sampleBooking);
//    }

    @Test
    void getActiveUnfinishedBookingsForRouteListing_Success_Return() {
        // arrange
        Booking sampleBooking1 = testUtils.createSampleBooking1();
        Booking sampleBooking2 = testUtils.createSampleBooking2();
        List<Booking> bookingList = new ArrayList<>();
        bookingList.add(sampleBooking1);
        bookingList.add(sampleBooking2);

        RouteListingPk sampleRouteListingPk = testUtils.createSampleRouteListingPk1();

        // mock
        when(bookingRepo.findAllByOutboundRouteListingRouteListingPkAndIsPaidFalse(any(RouteListingPk.class)))
                .thenReturn(bookingList);

        // act
        List<Booking> bookings = bookingService.getActiveUnfinishedBookingsForRouteListing(sampleRouteListingPk);

        // assert
        assertNotNull(bookings);

        // verify
        verify(bookingRepo).findAllByOutboundRouteListingRouteListingPkAndIsPaidFalse(any(RouteListingPk.class));
    }

    @Test
    void calculateAndSaveChargedPrice_Success_Return() {
        // arrange
        Booking sampleBooking = testUtils.createSampleBooking1();

        SeatListing sampleSeatListing1 = testUtils.createSampleSeatListing1();
        SeatListing sampleSeatListing2 = testUtils.createSampleSeatListing2();
        List<SeatListing> sampleSeatListings = new ArrayList<>();
        sampleSeatListings.add(sampleSeatListing1);
        sampleSeatListings.add(sampleSeatListing2);

        sampleBooking.setSeatListing(sampleSeatListings);

        // mock
        when(bookingRepo.findById(any(Integer.class))).thenReturn(Optional.of(sampleBooking));

        // act
        double price = bookingService.calculateAndSaveChargedPrice(sampleBooking.getBookingId());

        // assert
        assertEquals(sampleBooking.getChargedPrice(), price);

        // verify
        verify(bookingRepo, times(2)).findById(sampleBooking.getBookingId());
    }

    @Test
    void markBookingAsPaid_Success() {
        // arrange
        Booking sampleBooking = testUtils.createSampleBooking1();

        SeatListing sampleSeatListing1 = testUtils.createSampleSeatListing1();
        SeatListing sampleSeatListing2 = testUtils.createSampleSeatListing2();
        List<SeatListing> sampleSeatListings = new ArrayList<>();
        sampleSeatListings.add(sampleSeatListing1);
        sampleSeatListings.add(sampleSeatListing2);

        sampleBooking.setSeatListing(sampleSeatListings);

        // mock
        when(bookingRepo.findById(any(Integer.class))).thenReturn(Optional.of(sampleBooking));
        when(bookingRepo.save(any(Booking.class))).thenReturn(sampleBooking);

        // act
        bookingService.markBookingAsPaid(sampleBooking.getBookingId());

        // verify
        verify(bookingRepo).findById(sampleBooking.getBookingId());
        verify(bookingRepo).save(sampleBooking);
    }

    // TODO: Write tests for getPriceBreakdown
}
