package com.G2T5203.wingit.seatListing;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class SeatListingNotFoundException extends RuntimeException {
    SeatListingNotFoundException(SeatListingPk seatListing) {
        super("Could not find seatlisting " + seatListing);
    }
    SeatListingNotFoundException(String msg) {
        super(msg);
    }
}
