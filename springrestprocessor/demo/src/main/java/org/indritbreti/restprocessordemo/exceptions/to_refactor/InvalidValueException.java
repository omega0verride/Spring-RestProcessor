package org.indritbreti.restprocessordemo.exceptions.to_refactor;

import org.indritbreti.restprocessordemo.exceptions.BaseException;
import org.indritbreti.restprocessordemo.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class InvalidValueException extends BaseException {
    public InvalidValueException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
