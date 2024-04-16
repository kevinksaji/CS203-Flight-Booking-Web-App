package com.G2T5203.wingit.seatListing;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class SeatListingBadRequestException extends RuntimeException {
    public SeatListingBadRequestException(Exception e) {
        super("BAD REQUEST: " + e.getLocalizedMessage());
    }

    public SeatListingBadRequestException(String msg) {
        super("BAD REQUEST: " + msg);
    }
}
