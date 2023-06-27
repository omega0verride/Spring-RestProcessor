package org.indritbreti.restprocessordemo.exceptions.api;

import org.indritbreti.restprocessordemo.exceptions.BaseException;
import org.indritbreti.restprocessordemo.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class ResourceAlreadyExistsException extends BaseException {
    public String resourceName;
    public String fieldName;
    public Object fieldValue;

    public ResourceAlreadyExistsException(String resourceName, String fieldName, Object fieldValue) {
        super(HttpStatus.CONFLICT, String.format("%s with %s: '%s' already exists.", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }
}
