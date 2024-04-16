package com.G2T5203.wingit.booking;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BookingBadRequestException extends RuntimeException {
    public BookingBadRequestException(Exception e) { super("BAD REQUEST: " + e.getLocalizedMessage()); }
    public BookingBadRequestException(String msg) { super("BAD REQUEST: " + msg); }
}
