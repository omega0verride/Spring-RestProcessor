package org.indritbreti.restprocessordemo.exceptions.mailSender;

import org.indritbreti.restprocessordemo.exceptions.BaseException;
import org.indritbreti.restprocessordemo.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public abstract class EmailSenderException extends BaseException {
    public EmailSenderException(HttpStatus httpStatus, String message) {
        super(httpStatus, message);
    }

    public EmailSenderException(HttpStatus httpStatus) {
        super(httpStatus);
    }

    public EmailSenderException() {
        super();
    }
}