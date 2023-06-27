package org.indritbreti.restprocessordemo.exceptions.api.unauthorized;

import org.indritbreti.restprocessordemo.exceptions.BaseException;
import org.indritbreti.restprocessordemo.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class TokenExpiredException extends BaseException {
    public TokenExpiredException(com.auth0.jwt.exceptions.TokenExpiredException rootTokenExpiredException) {
        super(HttpStatus.UNAUTHORIZED, rootTokenExpiredException.getMessage());
        setRootException(rootTokenExpiredException);
    }
    // removed because this causes inconsistency in ex. messages if used incorrectly
    //    public TokenExpiredException(){
    //        super(HttpStatus.UNAUTHORIZED, "Authentication Failed! Token Expired!");
    //    }
}
