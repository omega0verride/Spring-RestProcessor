package org.indritbreti.restprocessordemo.exceptions.api.forbidden;

import org.indritbreti.restprocessordemo.exceptions.BaseException;
import lombok.Getter;
import lombok.Setter;
import org.indritbreti.restprocessordemo.exceptions.BaseException;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class ForbiddenAccessException extends BaseException {
    public ForbiddenAccessException(String message) {
        super(HttpStatus.FORBIDDEN, message);
    }
}
