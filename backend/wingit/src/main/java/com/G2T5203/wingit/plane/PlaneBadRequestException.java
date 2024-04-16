package com.G2T5203.wingit.plane;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PlaneBadRequestException extends RuntimeException {
    public PlaneBadRequestException(Exception e) { super("BAD REQUEST: " + e.getLocalizedMessage());}
    public PlaneBadRequestException(String msg) { super("BAD REQUEST: " + msg);}
}
