package org.indritbreti.restprocessordemo.exceptions.api.unauthorized;

import org.indritbreti.restprocessordemo.exceptions.BaseException;
import org.indritbreti.restprocessordemo.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class InvalidCredentialsException extends BaseException {
    public InvalidCredentialsException(Exception rootException) {
        this("Invalid Credentials! Check username/password.'", rootException);
    }

    public InvalidCredentialsException(String message) {
        this(message, null);
    }

    public InvalidCredentialsException(String message, Exception rootException) {
        super(HttpStatus.UNAUTHORIZED, message);
        setRootException(rootException);
    }

    public InvalidCredentialsException() {
        this((String) null);
    }
}
