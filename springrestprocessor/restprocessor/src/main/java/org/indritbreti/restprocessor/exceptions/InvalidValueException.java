package org.indritbreti.restprocessor.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidValueException extends BaseException {
    public InvalidValueException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
