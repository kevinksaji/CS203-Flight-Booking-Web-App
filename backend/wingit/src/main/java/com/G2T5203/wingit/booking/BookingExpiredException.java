package com.G2T5203.wingit.booking;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BookingExpiredException extends RuntimeException {
    public BookingExpiredException() { super("BOOKING HAS EXPIRED. And is now deleted."); }
}
