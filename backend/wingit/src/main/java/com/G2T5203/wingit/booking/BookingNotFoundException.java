package com.G2T5203.wingit.booking;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class BookingNotFoundException extends RuntimeException {
    public BookingNotFoundException(int bookingId) { super("Could not find booking " + bookingId); }
}
