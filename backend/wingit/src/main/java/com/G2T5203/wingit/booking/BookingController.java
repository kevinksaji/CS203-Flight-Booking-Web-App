package com.G2T5203.wingit.booking;

import com.G2T5203.wingit.user.UserBadRequestException;
import com.G2T5203.wingit.utils.DateUtils;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class BookingController {
    private final BookingService service;

    public BookingController(BookingService service) {
        this.service = service;
    }


    // TODO: Extract this function into common utilities (this is same code copied from UserController.
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
    public void checkIfNotUserNorAdmin(String username, UserDetails userDetails, Jwt jwt) {
        if (jwt != null) {
            if (isNeitherUserNorAdmin(username, jwt)) throw new UserBadRequestException("Not the same user.");
        } else if (userDetails != null) {
            if (isNeitherUserNorAdmin(username, userDetails)) throw new UserBadRequestException("Not the same user.");
        } else {
            throw new UserBadRequestException("No AuthenticationPrincipal provided for check.");
        }
    }


    @GetMapping(path = "/bookings/{username}")
    public List<BookingSimpleJson> getUserBookings(@PathVariable String username,
                                                   @AuthenticationPrincipal UserDetails userDetails, @AuthenticationPrincipal Jwt jwt) {
        checkIfNotUserNorAdmin(username, userDetails, jwt);

        return service.getAllBookingsByUser(username);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "bookings/new")
    public BookingSimpleJson createBooking(@Valid @RequestBody BookingSimpleJson newBookingSimpleJson) {
        try {
            return service.createBooking(newBookingSimpleJson);
        } catch (Exception e) {
            throw new BookingBadRequestException(e);
        }
    }

    @DeleteMapping("bookings/delete/{bookingId}")
    public void deleteBooking(@PathVariable int bookingId,
                              @AuthenticationPrincipal UserDetails userDetails,
                              @AuthenticationPrincipal Jwt jwt) {
        checkIfNotUserNorAdmin(service.getBookingUserUsername(bookingId), userDetails, jwt);
        try {
            service.deleteBookingById(bookingId);
        } catch (BookingNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new BookingBadRequestException(e);
        }
    }

    @PutMapping("bookings/calculateChargedPrice/{bookingId}")
    public Map<String, Object> calculateAndSaveChargedPrice(@PathVariable int bookingId) {
        try {
            Map<String, Object> priceResult = new HashMap<>();
            double totalChargedPrice = service.calculateAndSaveChargedPrice(bookingId);
            priceResult.put("totalChargedPrice", totalChargedPrice);
            return priceResult;
        } catch (BookingNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new BookingBadRequestException(e);
        }
    }

    @PutMapping("bookings/markAsPaid/{bookingId}")
    public void markBookingAsPaid(@PathVariable int bookingId,
                                  @AuthenticationPrincipal UserDetails userDetails,
                                  @AuthenticationPrincipal Jwt jwt) {
        checkIfNotUserNorAdmin(service.getBookingUserUsername(bookingId), userDetails, jwt);
        try {
            service.markBookingAsPaid(bookingId);
        } catch (BookingNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new BookingBadRequestException(e);
        }
    }

    @GetMapping("bookings/getPriceBreakdown/{bookingId}")
    public Map<String, Object> getPriceBreakdown(@PathVariable int bookingId,
                                                 @AuthenticationPrincipal UserDetails userDetails,
                                                 @AuthenticationPrincipal Jwt jwt) {
        checkIfNotUserNorAdmin(service.getBookingUserUsername(bookingId), userDetails, jwt);
        try {
            return service.calculateCostBreakdownForBooking(bookingId);
        } catch (BookingNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new BookingBadRequestException(e);
        }
    }
}
