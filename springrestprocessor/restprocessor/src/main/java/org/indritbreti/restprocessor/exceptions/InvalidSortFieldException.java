package org.indritbreti.restprocessor.exceptions;
import org.springframework.http.HttpStatus;

import java.util.Set;


public class InvalidSortFieldException extends BaseException {
    public String field;
    public Set<String> sortableFields;

    public InvalidSortFieldException(String field, Set<String> sortableFields) {
        super(HttpStatus.BAD_REQUEST, "Invalid Sort Field! Sorting not supported, disabled or invalid field '" + field + "'");
        this.field = field;
        this.sortableFields = sortableFields;
    }
}
