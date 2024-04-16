package com.G2T5203.wingit.seat;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class SeatBadRequestException extends RuntimeException {
    public SeatBadRequestException(Exception e) {
        super("BAD REQUEST: " + e.getLocalizedMessage());
    }

    public SeatBadRequestException(String msg) {
        super("BAD REQUEST: " + msg);
    }
}
