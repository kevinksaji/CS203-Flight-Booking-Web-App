package com.G2T5203.wingit.route;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class RouteNotFoundException extends RuntimeException {
    public RouteNotFoundException(Integer id) { super("Could not find route " + id); }
}
