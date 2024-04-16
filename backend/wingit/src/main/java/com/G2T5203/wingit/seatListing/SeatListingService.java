package com.G2T5203.wingit.seatListing;

import com.G2T5203.wingit.booking.*;
import com.G2T5203.wingit.plane.Plane;
import com.G2T5203.wingit.plane.PlaneNotFoundException;
import com.G2T5203.wingit.plane.PlaneRepository;
import com.G2T5203.wingit.route.Route;
import com.G2T5203.wingit.route.RouteNotFoundException;
import com.G2T5203.wingit.route.RouteRepository;
import com.G2T5203.wingit.routeListing.RouteListing;
import com.G2T5203.wingit.routeListing.RouteListingNotFoundException;
import com.G2T5203.wingit.routeListing.RouteListingPk;
import com.G2T5203.wingit.routeListing.RouteListingRepository;
import com.G2T5203.wingit.seat.Seat;
import com.G2T5203.wingit.seat.SeatNotFoundException;
import com.G2T5203.wingit.seat.SeatPk;
import com.G2T5203.wingit.seat.SeatRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SeatListingService {
    private final SeatListingRepository repo;
    private final PlaneRepository planeRepo;
    private final RouteRepository routeRepo;
    private final RouteListingRepository routeListingRepo;
    private final SeatRepository seatRepo;
    private final BookingRepository bookingRepo;


    // NOTE: We cannot include booking service as it would introduce cyclic dependency


    public SeatListingService(SeatListingRepository repo, PlaneRepository planeRepo, RouteRepository routeRepo, RouteListingRepository routeListingRepo, SeatRepository seatRepo, BookingRepository bookingRepo) {
        this.repo = repo;
        this.planeRepo = planeRepo;
        this.routeRepo = routeRepo;
        this.routeListingRepo = routeListingRepo;
        this.seatRepo = seatRepo;
        this.bookingRepo = bookingRepo;
    }

    public boolean checkIsBookingFinalized(int bookingId) {
        Optional<Booking> optionalRetrievedBooking = bookingRepo.findById(bookingId);
        if (optionalRetrievedBooking.isEmpty()) throw new BookingNotFoundException(bookingId);
        Booking retrievedBooking = optionalRetrievedBooking.get();

        return retrievedBooking.isPaid();
    }

    public List<PrivacySeatListingSimpleJson> getAllSeatListingsInRouteListing(String planeId, int routeId, LocalDateTime departureDateTime) {
        Optional<Plane> retrievedPlane = planeRepo.findById(planeId);
        if (retrievedPlane.isEmpty()) throw new PlaneNotFoundException(planeId);

        Optional<Route> retrievedRoute = routeRepo.findById(routeId);
        if (retrievedRoute.isEmpty()) throw new RouteNotFoundException(routeId);

        RouteListingPk retrievedRoutListingPk = new RouteListingPk(retrievedPlane.get(), retrievedRoute.get(), departureDateTime);
        List<SeatListing> matchingSeatListings = repo.findBySeatListingPkRouteListingRouteListingPk(retrievedRoutListingPk);
        if (matchingSeatListings.isEmpty()) throw new SeatListingNotFoundException("No seatListing with sepcified routeListingPk");

        return matchingSeatListings.stream()
                .map(PrivacySeatListingSimpleJson::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public SeatListing createSeatListing(SeatListingSimpleJson newSeatListingSimpleJson) {

        // First, check if Plane exists
        Optional<Plane> retrievedPlane = planeRepo.findById(newSeatListingSimpleJson.getPlaneId());
        if (retrievedPlane.isEmpty()) throw new PlaneNotFoundException(newSeatListingSimpleJson.getPlaneId());

        // Second, check if Route exists
        Optional<Route> retrievedRoute = routeRepo.findById(newSeatListingSimpleJson.getRouteId());
        if (retrievedRoute.isEmpty()) throw new RouteNotFoundException(newSeatListingSimpleJson.getRouteId());

        // Third, check if RouteListing exists --> Create a RouteListingPk first to check
        RouteListingPk thisRouteListingPk = new RouteListingPk(retrievedPlane.get(), retrievedRoute.get(), newSeatListingSimpleJson.getDepartureDatetime());
        Optional<RouteListing> retrievedRouteListing = routeListingRepo.findById(thisRouteListingPk);
        if (retrievedRouteListing.isEmpty()) throw new RouteListingNotFoundException(thisRouteListingPk);

        // Fourth, check if Seat exists --> Create a Seat first to check
        SeatPk thisSeatPk = new SeatPk(retrievedPlane.get(), newSeatListingSimpleJson.getSeatNumber());
        Optional<Seat> retrievedSeat = seatRepo.findById(thisSeatPk);
        if (retrievedSeat.isEmpty()) throw new SeatNotFoundException(thisSeatPk);

        // By this time we have guaranteed the fields that consist a SeatListingPk exists
        // So create a SeatListingPk to check if it already exists
        SeatListingPk seatListingPk = new SeatListingPk(retrievedRouteListing.get(), retrievedSeat.get());
        boolean alreadyExists = repo.existsById(seatListingPk);
        if (alreadyExists) throw new SeatListingBadRequestException("Seatlisting already exists.");

        // By here, we have guaranteed that we can create a SeatListing
        // So create it and save
        // But remember that when creating a SeatListing, bookingId and occupantName is null
        // Booking updated after payment
        // occupantName updated after inputting details
        SeatListing newSeatListing = new SeatListing(
                seatListingPk,
                null,
                null);
        return repo.save(newSeatListing);
    }

    @Transactional
    public void createSeatListingsForNewRouteListing(RouteListing newRouteListing) {
        if (repo.existsBySeatListingPkRouteListingRouteListingPk(newRouteListing.getRouteListingPk()))
            throw new SeatListingBadRequestException("SeatListings for newRouteListing already exists");

        // We assume route, plane, etc. exists because we are passing in a proper RouteListing.
        Plane plane = newRouteListing.getRouteListingPk().getPlane();
        List<Seat> seats = seatRepo.findAllBySeatPkPlanePlaneId(plane.getPlaneId());

        int numSeats = seats.size();
        List<SeatListing> seatListingsToBeCreated = new ArrayList<>(numSeats);
        for (int i = 0; i < numSeats; i++) {
            Seat currentSeat = seats.get(i);
            seatListingsToBeCreated.add(new SeatListing(
                    new SeatListingPk(newRouteListing, currentSeat),
                    null,
                    null
            ));
        }

        repo.saveAll(seatListingsToBeCreated);
    }

    @Transactional
    public SeatListing reserveSeatListing(String planeId, int routeId, LocalDateTime departureDateTime, String seatNumber, Integer bookingId) {
        // Retrieve routeListing, check if exists
        Optional<Plane> retrievedPlane = planeRepo.findById(planeId);
        if (retrievedPlane.isEmpty()) throw new PlaneNotFoundException(planeId);

        Optional<Route> retrievedRoute = routeRepo.findById(routeId);
        if (retrievedRoute.isEmpty()) throw new RouteNotFoundException(routeId);

        RouteListingPk routeListingPk = new RouteListingPk(retrievedPlane.get(), retrievedRoute.get(), departureDateTime);
        Optional<RouteListing> retrievedRouteListing = routeListingRepo.findById(routeListingPk);
        if (retrievedRouteListing.isEmpty()) throw new RouteListingNotFoundException(routeListingPk);

        // Retrieve booking, check if exists
        Optional<Booking> retrievedBooking = bookingRepo.findById(bookingId);
        if (retrievedBooking.isEmpty()) throw new BookingNotFoundException(bookingId);

        // Retrieve booking's seatlisting (list). For each seatListing in it
        // such that the seatListing's routeListing's routeListingPk matches this routeListingPk
        // If matches, count++.
        // Afterwards, check if count < booking's partySize
        List<SeatListing> seatListings = retrievedBooking.get().getSeatListing();
        int count = 0;
        for (SeatListing seatListing : seatListings) {
            if (seatListing.getSeatListingPk().getRouteListing().getRouteListingPk().equals(routeListingPk)) {
                count++;
            }
        }

        if (count >= retrievedBooking.get().getPartySize()) {
            throw new SeatListingBadRequestException("Max number of seats have been selected");
        }

        return setSeatListing(planeId, routeId, departureDateTime, seatNumber, bookingId, null);
    }
    @Transactional
    public SeatListing setOccupantForSeatListing(String planeId, int routeId, LocalDateTime departureDateTime, String seatNumber, Integer bookingId, String occupantName) {
        return setSeatListing(planeId, routeId, departureDateTime, seatNumber, bookingId, occupantName);
    }
    private SeatListing setSeatListing(String planeId, int routeId, LocalDateTime departureDateTime, String seatNumber, Integer bookingId, String occupantName) {
        return setSeatListing(planeId, routeId, departureDateTime, seatNumber, bookingId, occupantName, false);
    }

    @Transactional
    private SeatListing setSeatListing(String planeId, int routeId, LocalDateTime departureDateTime, String seatNumber, Integer bookingId, String occupantName, boolean forced) {
        if (!forced &&
                bookingId != null && checkIsBookingFinalized(bookingId))
            throw new SeatListingBadRequestException("Trying to change seat listing reservation after booking finalization.");

        Optional<Plane> retrievedPlane = planeRepo.findById(planeId);
        if (retrievedPlane.isEmpty()) throw new PlaneNotFoundException(planeId);

        Optional<Route> retrievedRoute = routeRepo.findById(routeId);
        if (retrievedRoute.isEmpty()) throw new RouteNotFoundException(routeId);

        RouteListingPk routeListingPk = new RouteListingPk(retrievedPlane.get(), retrievedRoute.get(), departureDateTime);
        Optional<RouteListing> retrievedRouteListing = routeListingRepo.findById(routeListingPk);
        if (retrievedRouteListing.isEmpty()) throw new RouteListingNotFoundException(routeListingPk);

        SeatPk seatPk = new SeatPk(retrievedPlane.get(), seatNumber);
        Optional<Seat> retrievedSeat = seatRepo.findById(seatPk);
        if (retrievedSeat.isEmpty()) throw new SeatNotFoundException(seatPk);

        SeatListingPk seatListingPk = new SeatListingPk(retrievedRouteListing.get(), retrievedSeat.get());
        Optional<SeatListing> retrievedSeatListing = repo.findById(seatListingPk);
        if (retrievedSeatListing.isEmpty()) throw new SeatListingNotFoundException(seatListingPk);


        SeatListing seatListing = retrievedSeatListing.get();
        if (bookingId != null) {
            Optional<Booking> retrievedBooking = bookingRepo.findById(bookingId);
            if (retrievedBooking.isEmpty()) throw new BookingNotFoundException(bookingId);

            // If bookingId given (setOccupantName called), check if it matches bookingId from the seatListing
            // Different means that user is updating occupantName to the wrong seat
            if (seatListing.getBooking() != null && // If it's null, means we are setting the bookingID value.
                    !seatListing.getBooking().getBookingId().equals(bookingId)) {
                throw new SeatListingBadRequestException("Invalid booking ID, booking ID does not match");
            } else if (seatListing.getBooking() == null && occupantName != null) { // If bookingId is null BUT occupant name is given (setOccupantName called wrongly) must reserve before setting name.
                throw new SeatListingBadRequestException("No booking made yet, set a booking first.");
            }

            // Another check: Ensure that this seatListing's routeListingPk matches either the
            // inbound or outbound routeListingPk, otherwise it is possible to add any seatListing to the booking
            if (!seatListing.getSeatListingPk().checkSeatBelongsToRouteListing(seatListing, retrievedBooking.get().getOutboundRouteListing().getRouteListingPk())) {
                if (!seatListing.getSeatListingPk().checkSeatBelongsToRouteListing(seatListing, retrievedBooking.get().getInboundRouteListing().getRouteListingPk())) {
                    // if reach here means that this seatListing's routeListing matches neither the outbound/inbound routeListing
                    // hence the seatListing does not match this booking
                    throw new SeatListingBadRequestException("SeatListing does not exist for this booking");
                }
            }

            seatListing.setBooking(retrievedBooking.get());

        } else { // bookingId was set to null so caller is trying to cancel booking.
            if (!forced) {
                if (seatListing.getBooking() == null)
                    throw new SeatListingBadRequestException("Trying to cancel seat listing when it wasn't set to a booking previously");
                else if (checkIsBookingFinalized(seatListing.getBooking().getBookingId()))
                    throw new SeatListingBadRequestException("Trying to cancel seat listing when booking is already finalized.");
            }
            seatListing.setBooking(null);
        }

        seatListing.setOccupantName(occupantName);
        repo.save(seatListing);

        return seatListing;
    }

    @Transactional
    public SeatListing cancelSeatListingBooking(String planeId, int routeId, LocalDateTime departureDateTime, String seatNumber) {
        return setSeatListing(planeId, routeId, departureDateTime, seatNumber, null, null);
    }


    @Transactional
    public SeatListing forceCancelSeatListingBooking(String planeId, int routeId, LocalDateTime departureDateTime, String seatNumber) {
        return setSeatListing(planeId, routeId, departureDateTime, seatNumber, null, null, true);
    }
}
