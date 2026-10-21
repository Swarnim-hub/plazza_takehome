package com.plazza.ridehailing.exception;

import org.springframework.http.HttpStatus;

public class DriverUnavailableException extends ApiException {
    public DriverUnavailableException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
