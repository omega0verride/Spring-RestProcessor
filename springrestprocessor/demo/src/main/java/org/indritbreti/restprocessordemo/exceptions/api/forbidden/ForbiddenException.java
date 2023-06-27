package org.indritbreti.restprocessordemo.exceptions.api.forbidden;

import org.indritbreti.restprocessordemo.exceptions.BaseException;
import org.indritbreti.restprocessordemo.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class ForbiddenException extends BaseException {
    public ForbiddenException(String message) {
        super(HttpStatus.FORBIDDEN, message);
    }
    public ForbiddenException() {
        super(HttpStatus.FORBIDDEN);
    }
}
