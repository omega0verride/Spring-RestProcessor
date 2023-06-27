package org.indritbreti.restprocessordemo.exceptions.to_refactor;

import org.indritbreti.restprocessordemo.exceptions.BaseException;
import org.indritbreti.restprocessordemo.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class ConstraintViolationException extends BaseException {

    public ConstraintViolationException(HttpStatus httpStatus, String message) {
        super(httpStatus, message);
    }
}
