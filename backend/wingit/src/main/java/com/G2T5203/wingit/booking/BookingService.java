package com.G2T5203.wingit.booking;

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
import com.G2T5203.wingit.seatListing.SeatListing;
import com.G2T5203.wingit.seatListing.SeatListingRepository;
import com.G2T5203.wingit.seatListing.SeatListingService;
import com.G2T5203.wingit.user.UserNotFoundException;
import com.G2T5203.wingit.user.UserRepository;
import com.G2T5203.wingit.user.WingitUser;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookingService {
    private final BookingRepository repo;
    private final UserRepository userRepo;
    private final PlaneRepository planeRepo;
    private final RouteRepository routeRepo;
    private final RouteListingRepository routeListingRepo;
    private final SeatListingService seatListingService;
    private final SeatListingRepository seatListingRepo;

    public BookingService(BookingRepository repo,
                          UserRepository userRepo,
                          PlaneRepository planeRepo,
                          RouteRepository routeRepo,
                          RouteListingRepository routeListingRepo,
                          SeatListingService seatListingService,
                          SeatListingRepository seatListingRepo) {
        this.repo = repo;
        this.userRepo = userRepo;
        this.planeRepo = planeRepo;
        this.routeRepo = routeRepo;
        this.routeListingRepo = routeListingRepo;
        this.seatListingService = seatListingService;
        this.seatListingRepo = seatListingRepo;
    }

    // get username of booking's user using bookingId
    public String getBookingUserUsername(int bookingId) {
        Optional<Booking> optionalRetrievedBooking = repo.findById(bookingId);
        if (optionalRetrievedBooking.isEmpty()) throw new BookingNotFoundException(bookingId);
        Booking retrievedBooking = optionalRetrievedBooking.get();
        if (isBookingExpired(retrievedBooking)) {
            deleteExpiredBooking(retrievedBooking);
            throw new BookingExpiredException();
        }

        return retrievedBooking.getWingitUser().getUsername();
    }

    public Booking getBookingById(int bookingId) {
        Optional<Booking> optionalBooking = repo.findById(bookingId);
        if (optionalBooking.isEmpty()) {
            throw new BookingNotFoundException(bookingId);
        }
        Booking booking = optionalBooking.get();
        return booking;
    }

    public boolean checkIsBookingFinalized(int bookingId) {
        Optional<Booking> optionalRetrievedBooking = repo.findById(bookingId);
        if (optionalRetrievedBooking.isEmpty()) throw new BookingNotFoundException(bookingId);
        Booking retrievedBooking = optionalRetrievedBooking.get();
        if (isBookingExpired(retrievedBooking)) {
            deleteExpiredBooking(retrievedBooking);
            throw new BookingExpiredException();
        }

        return retrievedBooking.isPaid();
    }

    // Get all the bookings under a user
    public List<BookingSimpleJson> getAllBookingsByUser(String username) {
        List<Booking> bookings = repo.findAllByWingitUserUsername(username);
        // Checking for expired bookings.
        boolean hasDeletedSomeExpiredBookings = false;
        for (Booking booking : bookings) {
            if (isBookingExpired(booking)) {
                deleteExpiredBooking(booking);
                hasDeletedSomeExpiredBookings = true;
            }
        }
        if (hasDeletedSomeExpiredBookings) {
            bookings = repo.findAllByWingitUserUsername(username);
        }

        return bookings.stream()
                .map(BookingSimpleJson::new)
                .collect(Collectors.toList());
    }

    // Needed to copy over this function from RouteListingService due to circular dependency error
    // (bookingService uses routeListingService uses bookingService)
    // Also makes sense for this to be here since we are grabbing a lot of data from the bookings as well to calculate
    // those currently booking.
    public int calculateRemainingSeatsForRouteListing(RouteListingPk routeListingPk) {
        List<SeatListing> availableSeats = seatListingRepo.findBySeatListingPkRouteListingRouteListingPkAndBookingIsNull(routeListingPk);
        List<Booking> activeBookingsForRouteListing = getActiveUnfinishedBookingsForRouteListing(routeListingPk);
        int numRemainingSeats = availableSeats.size();
        for (Booking booking : activeBookingsForRouteListing) {
            numRemainingSeats -= booking.getPartySize(); // Remove reserved number of seats for active booking
            // must do below because if unfinished booking reserves a seat, numRemainingSeats will be one less and double counted by party pax.
            int numSeatsMatchingRouteListing = 0;
            for (SeatListing seatListing : booking.getSeatListing()) {
                if (seatListing.getSeatListingPk().getRouteListing().getRouteListingPk().equals(routeListingPk)) {
                    numSeatsMatchingRouteListing++;
                }
            }
            numRemainingSeats += numSeatsMatchingRouteListing; // Add back to tally those they already booked to undo doublecount.
        }

        return numRemainingSeats;
    }

    @Transactional
    public BookingSimpleJson createBooking(BookingSimpleJson bookingSimpleJson) {
        Optional<WingitUser> retrievedUser = userRepo.findByUsername(bookingSimpleJson.getUsername());
        if (retrievedUser.isEmpty()) throw new UserNotFoundException(bookingSimpleJson.getUsername());

        // For outbound, get Plane, Route, and departureDatetime to form RouteListingPk
        // Afterwards use RouteListingPk to find RouteListing
        Optional<Plane> retrievedOutboundPlane = planeRepo.findById(bookingSimpleJson.getOutboundPlaneId());
        if (retrievedOutboundPlane.isEmpty()) throw new PlaneNotFoundException(bookingSimpleJson.getOutboundPlaneId());

        Optional<Route> retrievedOutboundRoute = routeRepo.findById(bookingSimpleJson.getOutboundRouteId());
        if (retrievedOutboundRoute.isEmpty()) throw new RouteNotFoundException(bookingSimpleJson.getOutboundRouteId());

        RouteListingPk thisOutboundRouteListingPk = new RouteListingPk(retrievedOutboundPlane.get(), retrievedOutboundRoute.get(), bookingSimpleJson.getOutboundDepartureDatetime());
        Optional<RouteListing> retrievedOutboundRouteListing = routeListingRepo.findById(thisOutboundRouteListingPk);
        if (retrievedOutboundRouteListing.isEmpty()) throw new RouteListingNotFoundException(thisOutboundRouteListingPk);

        // Check if routeListing has enough seatListings for Booking's partySize
        int bookingPax = bookingSimpleJson.getPartySize();
        int remainingSeatsForOutboundRouteListing = calculateRemainingSeatsForRouteListing(thisOutboundRouteListingPk);
        if (remainingSeatsForOutboundRouteListing < bookingPax) {
            throw new BookingBadRequestException("Outbound Routelisting has insufficient seats for selected pax");
        }


        // For inbound, get Plane, Route, and departureDatetime to form RouteListingPk
        // Afterwards use RouteListingPk to find RouteListing
        boolean hasInbound = bookingSimpleJson.getInboundDepartureDatetime() != null;
        Optional<Plane> retrievedInboundPlane = Optional.empty();
        Optional<Route> retrievedInboundRoute = Optional.empty();
        Optional<RouteListing> retrievedInboundRouteListing = Optional.empty();
        if (hasInbound) {
            retrievedInboundPlane = planeRepo.findById(bookingSimpleJson.getInboundPlaneId());
            if (retrievedInboundPlane.isEmpty()) throw new PlaneNotFoundException(bookingSimpleJson.getInboundPlaneId());

            retrievedInboundRoute = routeRepo.findById(bookingSimpleJson.getInboundRouteId());
            if (retrievedInboundRoute.isEmpty()) throw new RouteNotFoundException(bookingSimpleJson.getInboundRouteId());

            RouteListingPk thisInboundRouteListingPk = new RouteListingPk(
                    retrievedInboundPlane.get(),
                    retrievedInboundRoute.get(),
                    bookingSimpleJson.getInboundDepartureDatetime());
            retrievedInboundRouteListing = routeListingRepo.findById(thisInboundRouteListingPk);
            if (retrievedInboundRouteListing.isEmpty())
                throw new RouteListingNotFoundException(thisInboundRouteListingPk);

            // Check date of InboundRouteListing. If it is before OutboundRouteListing, throw exception
            // Do this by retrieving the outboundRouteListing's departureDatetime & compare with the inboundRouteListing departureDatetime
            LocalDateTime outboundDatetime = retrievedOutboundRouteListing.get().getRouteListingPk().getDepartureDatetime();
            LocalDateTime inboundDatetime = retrievedInboundRouteListing.get().getRouteListingPk().getDepartureDatetime();
            if (inboundDatetime.isBefore(outboundDatetime)) {
                throw new BookingBadRequestException("Inbound flight departure datetime is before outbound");
            }


            // Also check that the outbound departure == inbound arrival and outbound arrival = inbound departure.
            boolean correctInboundOutboundDestinations =
                    retrievedOutboundRouteListing.get().getRouteListingPk().getRoute().getArrivalDest().equals(
                        retrievedInboundRouteListing.get().getRouteListingPk().getRoute().getDepartureDest())
                    &&
                    retrievedOutboundRouteListing.get().getRouteListingPk().getRoute().getDepartureDest().equals(
                        retrievedInboundRouteListing.get().getRouteListingPk().getRoute().getArrivalDest());
            if (!correctInboundOutboundDestinations) {
                throw new BookingBadRequestException("Outbound destinations don't match flipped version of inbound destinations");
            }



            // Check if routeListing has enough seatListings for Booking's partySize
            int remainingSeatsForInboundRouteListing = calculateRemainingSeatsForRouteListing(thisInboundRouteListingPk);
            if (remainingSeatsForInboundRouteListing < bookingPax) {
                throw new BookingBadRequestException("Inbound Routelisting has insufficient seats for selected pax");
            }
        }


        // When creating, always force isPaid to false and chargedPrice as -1 to indicate that they are not yet set.
        bookingSimpleJson.setChargedPrice(-1.0);
        bookingSimpleJson.setPaid(false);

        // also always override startDateTime with server side LocalDateTime.
        bookingSimpleJson.setStartBookingDatetime(LocalDateTime.now());
        Booking newBooking = new Booking(
                retrievedUser.get(),
                retrievedOutboundRouteListing.get(),
                retrievedInboundRouteListing.orElse(null),
                bookingSimpleJson.getStartBookingDatetime(),
                bookingSimpleJson.getPartySize(),
                bookingSimpleJson.getChargedPrice(),
                false
        );

        Booking savedBooking = repo.save(newBooking);

        return new BookingSimpleJson(savedBooking);
    }

    @Transactional
    public void deleteBookingById(int bookingId) {
        if (repo.existsById(bookingId)) {
            repo.deleteById((bookingId));
        } else {
            throw new BookingNotFoundException(bookingId);
        }
    }

    @Transactional
    private void deleteExpiredBookingById(int bookingId) {
        Optional<Booking> booking = repo.findById(bookingId);
        if (booking.isPresent()) {
            deleteExpiredBooking(booking.get());
        } else {
            throw new BookingNotFoundException(bookingId);
        }
    }

    @Transactional
    private void deleteExpiredBooking(Booking booking) {
        try {
            for (SeatListing seatListing : booking.getSeatListing()) {
                // Cancel all seatListings if any.
                RouteListingPk routeListingPk = seatListing.getSeatListingPk().getRouteListing().getRouteListingPk();
                seatListingService.cancelSeatListingBooking(
                        routeListingPk.getPlane().getPlaneId(),
                        routeListingPk.getRoute().getRouteId(),
                        routeListingPk.getDepartureDatetime(),
                        seatListing.getSeatListingPk().getSeat().getSeatPk().getSeatNumber());
            }
            repo.deleteById((booking.getBookingId()));
        } catch (Exception e) {
            throw new BookingBadRequestException(e);
        }
    }

    @Transactional
    public void forceDeleteBooking(Booking booking) {
        try {
            for (SeatListing seatListing : booking.getSeatListing()) {
                RouteListingPk routeListingPk = seatListing.getSeatListingPk().getRouteListing().getRouteListingPk();
                seatListingService.forceCancelSeatListingBooking(
                        routeListingPk.getPlane().getPlaneId(),
                        routeListingPk.getRoute().getRouteId(),
                        routeListingPk.getDepartureDatetime(),
                        seatListing.getSeatListingPk().getSeat().getSeatPk().getSeatNumber());
            }
            // NOTE: Safety clearing. Cause this cached version of booking still has the seats.
            // If we delete it without first clearing, it might also cause the seats to be resetted.
            // Effectively undoing the forced Cancel.
            List<SeatListing> bookingSeatListing = booking.getSeatListing();
            bookingSeatListing.clear();
            booking.setSeatListing(bookingSeatListing);
            repo.deleteById(booking.getBookingId());
        } catch (Exception e) {
            throw new BookingBadRequestException(e);
        }
    }

    @Transactional
    public List<Booking> getActiveUnfinishedBookingsForRouteListing(RouteListingPk routeListingPk) {
        List<Booking> matchingUnfinishedOutboundRouteListing = repo.findAllByOutboundRouteListingRouteListingPkAndIsPaidFalse(routeListingPk);
        List<Booking> matchingUnfinishedInboundRouteListing = repo.findAllByInboundRouteListingRouteListingPkAndIsPaidFalse(routeListingPk);

        List<Booking> matchingBookings = new ArrayList<>(matchingUnfinishedOutboundRouteListing.size() + matchingUnfinishedInboundRouteListing.size());
        matchingBookings.addAll(matchingUnfinishedOutboundRouteListing);
        matchingBookings.addAll(matchingUnfinishedInboundRouteListing);

        List<Booking> activeUnfinishedBookings = new ArrayList<>();

        for (Booking booking : matchingBookings) {
            if (isBookingExpired(booking)) {
                deleteExpiredBooking(booking);
            } else {
                activeUnfinishedBookings.add(booking);
            }
        }

        return activeUnfinishedBookings;
    }

    private boolean isBookingExpired(Booking booking) {
        if (booking.isPaid()) return false;
        final int MAX_DURATION_IN_MINUTES = 15;
        boolean isPastExpiry = Duration.between(booking.getStartBookingDatetime(), LocalDateTime.now()).toMinutes() > MAX_DURATION_IN_MINUTES;
        // Only expired if it's not paid and past the timings.
        return isPastExpiry && !booking.isPaid();
    }

    private void throwIfSeatBookingsIncomplete(Booking booking) throws BookingBadRequestException {
        // retrieve outbound & inbound routeListings
        // count number of seatListing's routeListingPks that matches outboundRoutelistingPk
        // count number of seatListing's routeListingPks that matches inboundRoutelistingPk
        // check if count == count == booking's partySize
        RouteListingPk outboundRouteListingPk = booking.getOutboundRouteListing().getRouteListingPk();
        int outboundSeatListingCount = 0;
        for (SeatListing seatListing : booking.getSeatListing()) {
            if (seatListing.getSeatListingPk().checkSeatBelongsToRouteListing(seatListing, outboundRouteListingPk)) {
                outboundSeatListingCount++;
            }
        }
        if (outboundSeatListingCount != booking.getPartySize()) {
            throw new BookingBadRequestException("Incorrect number of seats booked for partySize.");
        }
        if (booking.hasInboundRouteListing()) {
            RouteListingPk inboundRouteListingPk = booking.getInboundRouteListing().getRouteListingPk();
            int inboundSeatListingCount = 0;
            for (SeatListing seatListing : booking.getSeatListing()) {
                if (seatListing.getSeatListingPk().checkSeatBelongsToRouteListing(seatListing, inboundRouteListingPk)) {
                    inboundSeatListingCount++;
                }
            }
            if (outboundSeatListingCount != inboundSeatListingCount) {
                throw new BookingBadRequestException("Number of outbound seatListings does not match number of inbound seatListings.");
            }
        }
    }

    @Transactional
    public double calculateAndSaveChargedPrice(int bookingId) {
        if (checkIsBookingFinalized(bookingId)) throw new BookingBadRequestException("Trying to change price after booking is finalised");

        Optional<Booking> bookingOptional = repo.findById(bookingId);
        if (bookingOptional.isPresent()) {
            Booking booking = bookingOptional.get();
            if (isBookingExpired(booking)) {
                deleteExpiredBooking(booking);
                throw new BookingExpiredException();
            }
            // TODO: Should we be checking if chargedPrice < 0? Or do we allow multiple recalculations.
            throwIfSeatBookingsIncomplete(booking);

            double outboundPriceTotal = 0.0;
            double inboundPriceTotal = 0.0;
            final double outboundBasePrice = booking.getOutboundRouteListing().getBasePrice();
            final double inboundBasePrice = booking.hasInboundRouteListing() ? booking.getInboundRouteListing().getBasePrice() : 0.0;
            for (SeatListing seatListing : booking.getSeatListing()) {
                boolean isOutboundRouteListing = seatListing.getSeatListingPk().getRouteListing().getRouteListingPk() == booking.getOutboundRouteListing().getRouteListingPk();
                if (isOutboundRouteListing) {
                    outboundPriceTotal += seatListing.getSeatListingPk().getSeat().getPriceFactor() * outboundBasePrice;
                } else {
                    inboundPriceTotal += seatListing.getSeatListingPk().getSeat().getPriceFactor() * inboundBasePrice;
                }
            }
            double totalChargedPrice = outboundPriceTotal + inboundPriceTotal;
            booking.setChargedPrice(totalChargedPrice);
            repo.save(booking);

            return totalChargedPrice;
        } else {
            throw new BookingNotFoundException(bookingId);
        }
    }

    @Transactional
    public Map<String, Object> calculateCostBreakdownForBooking(int bookingId) {
        Optional<Booking> bookingOptional = repo.findById(bookingId);
        if (bookingOptional.isEmpty()) throw new BookingNotFoundException(bookingId);
        Booking booking = bookingOptional.get();
        if (isBookingExpired(booking)) {
            deleteExpiredBooking(booking);
            throw new BookingExpiredException();
        }
        throwIfSeatBookingsIncomplete(booking);



        Map<String, Object> costSummary = new HashMap<>();
        final double outboundBasePrice = booking.getOutboundRouteListing().getBasePrice();
        costSummary.put("outboundBasePrice", outboundBasePrice);
        final double inboundBasePrice = booking.hasInboundRouteListing() ? booking.getInboundRouteListing().getBasePrice() : 0.0;
        if (inboundBasePrice > 0.0) costSummary.put("inboundBasePrice", inboundBasePrice);
        for (SeatListing seatListing : booking.getSeatListing()) {
            boolean isOutboundRouteListing = seatListing.getSeatListingPk().getRouteListing().getRouteListingPk() == booking.getOutboundRouteListing().getRouteListingPk();
            Map<String, Object> seatBreakdown = new HashMap<>();
            seatBreakdown.put("SeatNumber", seatListing.getSeatListingPk().getSeat().getSeatPk().getSeatNumber());
            seatBreakdown.put("SeatClass", seatListing.getSeatListingPk().getSeat().getSeatClass());
            seatBreakdown.put("PriceFactor", seatListing.getSeatListingPk().getSeat().getPriceFactor());
            if (isOutboundRouteListing) {
                double outboundSeatPrice = seatListing.getSeatListingPk().getSeat().getPriceFactor() * outboundBasePrice;
                seatBreakdown.put("SeatPrice", outboundSeatPrice);
                costSummary.put(
                        "OUTBOUND-" + seatListing.getSeatListingPk().getSeat().getSeatPk().getSeatNumber(),
                        seatBreakdown);
            } else {
                double inboundSeatPrice = seatListing.getSeatListingPk().getSeat().getPriceFactor() * inboundBasePrice;
                seatBreakdown.put("SeatPrice", inboundSeatPrice);
                costSummary.put(
                        "INBOUND-" + seatListing.getSeatListingPk().getSeat().getSeatPk().getSeatNumber(),
                        seatBreakdown);
            }
        }
        costSummary.put("totalChargedPrice", booking.getChargedPrice());

        return costSummary;
    }

    @Transactional
    public void markBookingAsPaid(int bookingId) {
        Optional<Booking> bookingOptional = repo.findById(bookingId);
        if (bookingOptional.isPresent()) {
            // Since payment might take time here... let's skip the expiry check...?

            // TODO: Logic checks here... if we have time to integrate with stripe.
            //       Not sure how it works yet. When it comes to it then we figure out. But likely some logic here.
            Booking booking = bookingOptional.get();
            throwIfSeatBookingsIncomplete(booking);

            booking.setPaid(true);
            repo.save(booking);
        } else {
            throw new BookingNotFoundException(bookingId);
        }
    }
}
