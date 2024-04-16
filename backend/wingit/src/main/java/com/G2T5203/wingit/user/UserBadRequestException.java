package com.G2T5203.wingit.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserBadRequestException extends RuntimeException {
    public UserBadRequestException(Exception e) { super("BAD REQUEST: " + e.getLocalizedMessage()); }
    public UserBadRequestException(String msg) { super("BAD REQUEST: " + msg); }
}

