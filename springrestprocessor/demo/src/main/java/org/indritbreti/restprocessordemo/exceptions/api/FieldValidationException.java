package org.indritbreti.restprocessordemo.exceptions.api;

import org.indritbreti.restprocessordemo.exceptions.BaseException;
import org.indritbreti.restprocessordemo.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class FieldValidationException extends BaseException {

    public FieldValidationException() {
        super(HttpStatus.BAD_REQUEST, "");
    }

}
