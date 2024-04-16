package com.G2T5203.wingit.routeListing;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class RouteListingBadRequestException extends RuntimeException {
    public RouteListingBadRequestException(Exception e) {
        super("BAD REQUEST: " + e.getLocalizedMessage());
    }
    public RouteListingBadRequestException(String msg) {
        super("BAD REQUEST: " + msg);
    }
}
