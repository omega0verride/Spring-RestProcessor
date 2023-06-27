package org.indritbreti.restprocessordemo.exceptions.api;

import org.indritbreti.restprocessordemo.exceptions.BaseException;
import org.indritbreti.restprocessordemo.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class UserAccountAlreadyActivatedException extends BaseException {
    public UserAccountAlreadyActivatedException() {
        super(HttpStatus.CONFLICT, "This user account is already activated!");
    }
}
