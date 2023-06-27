package org.indritbreti.restprocessor.DynamicQueryBuilder.DynamicFilterBuilder.Filters;

import org.indritbreti.restprocessor.exceptions.BaseException;
import org.springframework.http.HttpStatus;

// TODO should extend internalBaseException after we create it
public class InvalidFilterException extends BaseException {
    Filter<?> filter;

    public InvalidFilterException(Filter<?> filter) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "Invalid filter configuration. Contact developers!");
        this.filter = filter;
    }
}
