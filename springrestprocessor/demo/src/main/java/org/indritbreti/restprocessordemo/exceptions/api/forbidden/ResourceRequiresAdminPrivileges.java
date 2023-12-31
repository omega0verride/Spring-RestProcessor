package org.indritbreti.restprocessordemo.exceptions.api.forbidden;

import org.indritbreti.restprocessordemo.exceptions.BaseException;
import org.indritbreti.restprocessordemo.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class ResourceRequiresAdminPrivileges extends BaseException {
    public String resourceName;
    public String fieldName;
    public String fieldValue;

    public ResourceRequiresAdminPrivileges(String resourceName, String fieldName, String fieldValue) {
        super(HttpStatus.FORBIDDEN, String.format("%s with %s: %s requires admin privileges.", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }
}
