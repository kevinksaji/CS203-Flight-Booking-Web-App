package com.G2T5203.wingit.adminUtils;

import com.G2T5203.wingit.WingitApplication;
import com.G2T5203.wingit.booking.Booking;
import com.G2T5203.wingit.booking.BookingRepository;
import com.G2T5203.wingit.booking.BookingService;
import com.G2T5203.wingit.booking.BookingSimpleJson;
import com.G2T5203.wingit.plane.PlaneRepository;
import com.G2T5203.wingit.route.RouteRepository;
import com.G2T5203.wingit.routeListing.RouteListingRepository;
import com.G2T5203.wingit.seat.SeatRepository;
import com.G2T5203.wingit.seatListing.SeatListing;
import com.G2T5203.wingit.seatListing.SeatListingRepository;
import com.G2T5203.wingit.user.UserRepository;
import com.G2T5203.wingit.user.WingitUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

@RestController
public class AdminUtilsController {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final BookingService bookingService;

    public AdminUtilsController(UserRepository userRepository, BookingRepository bookingRepository, BookingService bookingService) {
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.bookingService = bookingService;
    }

    @PutMapping(path = "/adminUtils/forceCancelNonInitBookings")
    public void forceCancelNonInitBookings() {
        List<Booking> bookingsToBeDeleted = bookingRepository.findAllByWingitUserUsernameNot("richman");
        for (Booking bookingToBeDeleted : bookingsToBeDeleted) {
            bookingService.forceDeleteBooking(bookingToBeDeleted);
        }
    }

    @PutMapping(path = "/adminUtils/resetBookingsAndUsers")
    public void resetBookingsAndUsers() {
        forceCancelNonInitBookings();
        List<WingitUser> sampleUsers = DatabaseInitializer.getSampleUserList();
        List<WingitUser> nonAdminUsers = userRepository.findAllByAuthorityRole("ROLE_USER");
        for (WingitUser nonAdminUser : nonAdminUsers) {
            boolean isSampleUser = false;
            for (WingitUser sampleUser : sampleUsers) {
                if (nonAdminUser.getUsername().equals(sampleUser.getUsername())) {
                    isSampleUser = true;
                    break;
                }
            }

            if (isSampleUser) continue;
            userRepository.deleteById(nonAdminUser.getUsername());
        }
    }

}
