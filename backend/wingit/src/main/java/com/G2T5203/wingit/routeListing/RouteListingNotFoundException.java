package com.G2T5203.wingit.routeListing;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class RouteListingNotFoundException extends RuntimeException {
    public RouteListingNotFoundException(RouteListingPk pk) {
        super("Could not find route " + pk);
    }
}
