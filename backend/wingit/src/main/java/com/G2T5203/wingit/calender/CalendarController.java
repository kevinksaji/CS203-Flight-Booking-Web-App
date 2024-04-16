package com.G2T5203.wingit.calender;

import java.io.IOException;
import java.time.*;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;

import com.G2T5203.wingit.booking.Booking;
import com.G2T5203.wingit.booking.BookingController;
import com.G2T5203.wingit.booking.BookingService;
import com.G2T5203.wingit.booking.BookingSimpleJson;
import com.G2T5203.wingit.routeListing.RouteListing;
import com.G2T5203.wingit.seatListing.SeatListing;
import com.G2T5203.wingit.user.*;
import net.fortuna.ical4j.model.property.*;
import org.slf4j.event.KeyValuePair;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.util.RandomUidGenerator;
import net.fortuna.ical4j.util.UidGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping(path = "/rest/api")
public class CalendarController {
    private final BookingService bookingService;
    private static final Logger logger = LoggerFactory.getLogger(CalendarController.class);
    private final UserService userService;


    public CalendarController(BookingService bookingService, UserService userService) {
        this.bookingService = bookingService;
        this.userService = userService;
    }

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

    @GetMapping(path = "/generate-calendar/{bookingId}")
    public ResponseEntity<Resource> generateCalendarFile(@PathVariable int bookingId,
                                                         @AuthenticationPrincipal UserDetails userDetails,
                                                         @AuthenticationPrincipal Jwt jwt) {
        try {
            logger.info("Entering generateCalendarFile");

            String bookingUsername = bookingService.getBookingUserUsername(bookingId);
            logger.info("Booking username: {}", bookingUsername);

            Booking booking = bookingService.getBookingById(bookingId);

            checkIfNotUserNorAdmin(bookingUsername, userDetails, jwt);

            // create a new iCalendar
            Calendar icsCalendar = new Calendar();
            icsCalendar.add(new ProdId("-//WingIt//iCal4j 1.0//EN"));

            String eventSummary = "WingIt Flight Booking";

            String userEmail = getUserEmail(bookingUsername);

            // Convinience parse using BookingSimpleJson
            BookingSimpleJson bookingSimpleJson = new BookingSimpleJson(booking);

            // create the Outbound Flight event
            VEvent outboundEvent = createICalEvent(booking.getOutboundRouteListing(), bookingSimpleJson.getOutboundSeatNumbers(), eventSummary, userEmail);
            icsCalendar.add(outboundEvent);

            // if there is a inbound flight event, create it
            if (booking.getInboundRouteListing() != null) {
                VEvent inboundEvent = createICalEvent(booking.getInboundRouteListing(), bookingSimpleJson.getInboundSeatNumbers(), eventSummary, userEmail);
                icsCalendar.add(inboundEvent);
            }

            // convert the iCalendar to a byte array
            byte[] calendarBytes = icsCalendar.toString().getBytes();

            // create a Resource from the byte array
            Resource resource = new ByteArrayResource(calendarBytes);

            // eet response headers for the calendar file download
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=mycalendar.ics");
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");

            // return the calendar file as a response
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (Exception e) {
            logger.error("Exception occurred: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ByteArrayResource(("Error: " + e.getMessage()).getBytes()));
        } finally {
            // Log method exit
            logger.info("Exiting generateCalendarFile");
        }
    }

    private VEvent createICalEvent(RouteListing routeListing, Map<String, String> seatMap, String eventSummary, String userEmail) {
        if (routeListing != null) {
            LocalDateTime departureDatetime = routeListing.getRouteListingPk().getDepartureDatetime();
            Duration flightDuration = routeListing.getRouteListingPk().getRoute().getFlightDuration();

            // convert LocalDateTime to ZonedDateTime in the default system time zone
            ZoneId systemDefaultZone = ZoneId.systemDefault();
            ZonedDateTime zonedDateTime = departureDatetime.atZone(systemDefaultZone);

            // convert ZonedDateTime to milliseconds
            Long startDateTimeInMillis = zonedDateTime.toInstant().toEpochMilli();
            Long endDateTimeInMillis = zonedDateTime
                    .plusHours(flightDuration.toHoursPart())
                    .plusMinutes(flightDuration.toMinutesPart())
                    .toInstant().toEpochMilli(); // Assuming the event duration is 1 hour

            java.util.Calendar calendarStartTime = new GregorianCalendar();
            calendarStartTime.setTimeInMillis(startDateTimeInMillis);

            // time zone info
            TimeZone tz = calendarStartTime.getTimeZone();
            ZoneId zid = tz.toZoneId();

            /* generate unique identifier */
            UidGenerator ug = new RandomUidGenerator();
            Uid uid = ug.generateUid();

            LocalDateTime start = LocalDateTime.ofInstant(calendarStartTime.toInstant(), zid);
            LocalDateTime end = LocalDateTime.ofInstant(Instant.ofEpochMilli(endDateTimeInMillis), zid);
            VEvent event = new VEvent(start, end, eventSummary);
            event.add(uid);

            Attendee attendee = new Attendee(userEmail);
            event.add(attendee);

            Organizer organizer = new Organizer();
            organizer.setValue("MAILTO:WingIt@world.com");
            event.add(organizer);

            Location location = new Location(
                    routeListing.getRouteListingPk().getRoute().getDepartureDest() +
                            " to " +
                            routeListing.getRouteListingPk().getRoute().getArrivalDest()
            );
            event.add(location);

            Description description = new Description();
            String planeId = routeListing.getRouteListingPk().getPlane().getPlaneId();
            StringBuilder descStrBuilder = new StringBuilder();
            descStrBuilder.append(planeId);
            descStrBuilder.append('\n');
            for (Map.Entry<String, String> kvp : seatMap.entrySet()) {
                descStrBuilder.append(kvp.getValue());
                descStrBuilder.append(" (");
                descStrBuilder.append(kvp.getKey());
                descStrBuilder.append(")\n");
            }

            description.setValue(descStrBuilder.toString());
            event.add(description);

            return event;
        } else {
            // case when there is no routeListing
            return null;
        }
    }

    private String getUserEmail(String username) {
        WingitUser user = userService.getById(username);

        if (user != null) {
            // Create a WingitUserSimpleJson instance and retrieve the email
            WingitUserSimpleJson userJson = new WingitUserSimpleJson(user);
            return userJson.getEmail();
        } else {
            throw new UserNotFoundException(username);
        }
    }
}


