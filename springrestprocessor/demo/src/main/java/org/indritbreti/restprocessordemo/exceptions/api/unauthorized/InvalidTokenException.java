package org.indritbreti.restprocessordemo.exceptions.api.unauthorized;;
import org.indritbreti.restprocessordemo.exceptions.generic.TokenDecodeException;
import org.indritbreti.restprocessordemo.exceptions.BaseException;
import org.indritbreti.restprocessordemo.exceptions.generic.TokenDecodeException;
import org.springframework.http.HttpStatus;

public class InvalidTokenException extends BaseException {
    public InvalidTokenException(TokenDecodeException rootTokenDecodeException) {
        super(HttpStatus.UNAUTHORIZED, "Could not decode token!");
        setRootException(rootTokenDecodeException);
    }

    public InvalidTokenException(String message, Exception rootException) {
        super(HttpStatus.UNAUTHORIZED, message);
        setRootException(rootException);
    }

    public InvalidTokenException(String message) {
        super(HttpStatus.UNAUTHORIZED, message);
    }
}

