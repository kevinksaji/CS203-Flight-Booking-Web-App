package com.G2T5203.wingit.seatListing;

import com.G2T5203.wingit.booking.BookingBadRequestException;
import com.G2T5203.wingit.booking.BookingService;
import com.G2T5203.wingit.utils.DateUtils;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
public class SeatListingController {
    private boolean isAdmin(UserDetails userDetails) { return userDetails.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN")); }
    private boolean isAdmin(Jwt jwt) { return jwt.getClaim("role").equals("ROLE_ADMIN"); }
    private boolean isNeitherUserNorAdmin(String username, UserDetails userDetails) {
        boolean isUser = username.equals(userDetails.getUsername());
        boolean isAdmin = isAdmin(userDetails);

        return (!isUser && !isAdmin);
    }
    private boolean isNeitherUserNorAdmin(String username, Jwt jwt) {
        boolean isUser = username.equals(jwt.getClaim("sub"));
        boolean isAdmin = isAdmin(jwt);

        return (!isUser && !isAdmin);
    }
    private void checkIfNotUserNorAdmin(String username, UserDetails userDetails, Jwt jwt) {
        if (jwt != null) {
            if (isNeitherUserNorAdmin(username, jwt)) throw new SeatListingBadRequestException("Not the same user.");
        } else if (userDetails != null) {
            if (isNeitherUserNorAdmin(username, userDetails)) throw new SeatListingBadRequestException("Not the same user.");
        } else {
            throw new SeatListingBadRequestException("No AuthenticationPrincipal provided for check.");
        }
    }

    private final SeatListingService service;
    private final BookingService bookingService;

    public SeatListingController(SeatListingService service, BookingService bookingService) {
        this.service = service;
        this.bookingService = bookingService;
    }

    @GetMapping(path = "/seatListings/matchingRouteListing/{planeId}/{routeId}/{departureDatetimeStr}")
    public List<PrivacySeatListingSimpleJson> getAllSeatListings(@PathVariable String planeId, @PathVariable Integer routeId, @PathVariable String departureDatetimeStr) {
        try {
            LocalDateTime departureDatetime = DateUtils.handledParseDateTime(departureDatetimeStr);
            return service.getAllSeatListingsInRouteListing(planeId, routeId, departureDatetime);
        } catch (Exception e) {
            throw new BookingBadRequestException(e);
        }
    }

    @PutMapping(path = "/seatListings/bookSeat/reserve")
    public SeatListingSimpleJson reserveSeatListing(@Valid @RequestBody SeatListingSimpleJson seatBookingInfo) {
        try {
            if (seatBookingInfo.occupantName != null) throw new SeatListingBadRequestException("Reservation does not need occupant name yet!");
            SeatListing updatedSeatListing = service.reserveSeatListing(
                    seatBookingInfo.getPlaneId(),
                    seatBookingInfo.routeId,
                    seatBookingInfo.departureDatetime,
                    seatBookingInfo.seatNumber,
                    seatBookingInfo.bookingId);
            return new SeatListingSimpleJson(updatedSeatListing);
        } catch (Exception e) {
            throw new SeatListingBadRequestException(e);
        }
    }

    @PutMapping(path = "/seatListings/bookSeat/setOccupant/{bookingId}")
    public SeatListingSimpleJson setOccupantForSeatListing(@PathVariable int bookingId, @Valid @RequestBody SeatListingSimpleJson seatBookingInfo,
                                                           @AuthenticationPrincipal UserDetails userDetails, @AuthenticationPrincipal Jwt jwt) {
        // get username of this Booking's user
        String username = bookingService.getBookingUserUsername(bookingId);

        checkIfNotUserNorAdmin(username, userDetails, jwt);
        try {
            if (seatBookingInfo.occupantName == null) throw new SeatListingBadRequestException("Occupant Name is Empty!");
            SeatListing updatedSeatListing = service.setOccupantForSeatListing(
                    seatBookingInfo.getPlaneId(),
                    seatBookingInfo.routeId,
                    seatBookingInfo.departureDatetime,
                    seatBookingInfo.seatNumber,
                    seatBookingInfo.bookingId,
                    seatBookingInfo.occupantName);
            return new SeatListingSimpleJson(updatedSeatListing);
        } catch (Exception e) {
            throw new SeatListingBadRequestException(e);
        }
    }

    @PutMapping(path = "/seatListings/cancelSeatBooking/{bookingId}")
    public SeatListingSimpleJson cancelSeatListingBooking(@PathVariable int bookingId, @Valid @RequestBody SeatListingSimpleJson seatBookingInfo,
                                                          @AuthenticationPrincipal UserDetails userDetails, @AuthenticationPrincipal Jwt jwt) {
        // get username of this Booking's user
        String username = bookingService.getBookingUserUsername(bookingId);

        checkIfNotUserNorAdmin(username, userDetails, jwt);
        try {
            SeatListing updatedSeatListing =  service.cancelSeatListingBooking(
                    seatBookingInfo.getPlaneId(),
                    seatBookingInfo.routeId,
                    seatBookingInfo.departureDatetime,
                    seatBookingInfo.seatNumber);
            return new SeatListingSimpleJson(updatedSeatListing);
        } catch (Exception e) {
            throw new SeatListingBadRequestException(e);
        }
    }
}
