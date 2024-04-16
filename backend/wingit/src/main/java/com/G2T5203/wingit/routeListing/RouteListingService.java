package com.G2T5203.wingit.routeListing;

import com.G2T5203.wingit.booking.Booking;
import com.G2T5203.wingit.booking.BookingService;
import com.G2T5203.wingit.plane.Plane;
import com.G2T5203.wingit.plane.PlaneNotFoundException;
import com.G2T5203.wingit.plane.PlaneRepository;
import com.G2T5203.wingit.route.Route;
import com.G2T5203.wingit.route.RouteNotFoundException;
import com.G2T5203.wingit.route.RouteRepository;
import com.G2T5203.wingit.seatListing.SeatListing;
import com.G2T5203.wingit.seatListing.SeatListingRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RouteListingService {
    private final RouteListingRepository repo;
    private final RouteRepository routeRepo;
    private final PlaneRepository planeRepo;
    private final SeatListingRepository seatListingRepo;
    private final BookingService bookingService;

    public RouteListingService(
            RouteListingRepository repo,
            RouteRepository routeRepo,
            PlaneRepository planeRepo,
            SeatListingRepository seatListingRepo,
            BookingService bookingService) {
        this.repo = repo;
        this.routeRepo = routeRepo;
        this.planeRepo = planeRepo;
        this.seatListingRepo = seatListingRepo;
        this.bookingService = bookingService;
    }

    public List<RouteListingSimpleJson> getAllRouteListings() {
        List<RouteListing> routeListings = repo.findAll();
        return routeListings.stream()
                .map(routeListing -> new RouteListingSimpleJson(routeListing, calculateRemainingSeatsForRouteListing(routeListing.getRouteListingPk())))
                .collect(Collectors.toList());
    }

    public List<RouteListingSimpleJson> getAllRouteListingsMatchingFullSearch(String departureDest, String arrivalDest, LocalDate matchingDate) {
        List<RouteListing> routeListings = repo.findByRouteListingPkRouteDepartureDestAndRouteListingPkRouteArrivalDest(departureDest, arrivalDest);
        return routeListings.stream().filter(routeListing -> {
            LocalDate routeListingDate = routeListing.getRouteListingPk().getDepartureDatetime().toLocalDate();
            return routeListingDate.equals(matchingDate);
        }).map(routeListing -> new RouteListingSimpleJson(routeListing, calculateRemainingSeatsForRouteListing(routeListing.getRouteListingPk()))).collect(Collectors.toList());
    }

    public int calculateRemainingSeatsForRouteListing(RouteListingPk routeListingPk) {
        List<SeatListing> availableSeats = seatListingRepo.findBySeatListingPkRouteListingRouteListingPkAndBookingIsNull(routeListingPk);
        List<Booking> activeBookingsForRouteListing = bookingService.getActiveUnfinishedBookingsForRouteListing(routeListingPk);
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
    public RouteListing createRouteListing(RouteListingSimpleJson simpleJson) {
        Optional<Route> retrievedRoute = routeRepo.findById(simpleJson.getRouteId());
        if (retrievedRoute.isEmpty()) throw new RouteNotFoundException(simpleJson.getRouteId());

        Optional<Plane> retrievedPlane = planeRepo.findById(simpleJson.getPlaneId());
        if (retrievedPlane.isEmpty()) throw new PlaneNotFoundException(simpleJson.getPlaneId());

        RouteListingPk routeListingPk = new RouteListingPk(
                retrievedPlane.get(),
                retrievedRoute.get(),
                simpleJson.getDepartureDatetime());
        boolean alreadyExists = repo.existsById(routeListingPk);
        if (alreadyExists) throw new RouteListingBadRequestException("RouteListing already exists.");

        RouteListing newRouteListing = new RouteListing(
                routeListingPk,
                simpleJson.getBasePrice());

        return repo.save(newRouteListing);
    }
}
