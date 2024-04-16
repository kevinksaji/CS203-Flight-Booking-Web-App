package com.G2T5203.wingit.plane;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PlaneNotFoundException extends RuntimeException {
    public PlaneNotFoundException(String id) {
        super("Could not find plane " + id);
    }
}
