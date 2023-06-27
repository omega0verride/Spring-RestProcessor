package org.indritbreti.restprocessordemo.exceptions.api;

import org.indritbreti.restprocessordemo.exceptions.BaseException;
import org.indritbreti.restprocessordemo.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class ConfirmationTokenNotFoundException extends BaseException {
    public ConfirmationTokenNotFoundException() {
        super(HttpStatus.CONFLICT, "The confirmation token is invalid, has already been used or is expired!");
    }
}
