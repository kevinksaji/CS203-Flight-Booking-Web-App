package com.G2T5203.wingit.route;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class RouteBadRequestException extends RuntimeException {
    public RouteBadRequestException(Exception e) { super("BAD REQUEST: " + e.getLocalizedMessage()); }
    public RouteBadRequestException(String msg) { super("BAD REQUEST: " + msg); }
}
