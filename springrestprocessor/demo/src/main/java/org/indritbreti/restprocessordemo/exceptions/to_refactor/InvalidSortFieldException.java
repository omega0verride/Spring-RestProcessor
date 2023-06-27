package org.indritbreti.restprocessordemo.exceptions.to_refactor;

import org.indritbreti.restprocessordemo.exceptions.BaseException;
import lombok.Getter;
import lombok.Setter;
import org.indritbreti.restprocessordemo.exceptions.BaseException;
import org.springframework.http.HttpStatus;

import java.util.Set;

@Getter
@Setter
public class InvalidSortFieldException extends BaseException {
    public String field;
    public Set<String> sortableFields;

    public InvalidSortFieldException(String field, Set<String> sortableFields) {
        super(HttpStatus.BAD_REQUEST, "Invalid Sort Field! Sorting not supported or invalid field '" + field + "'");
        this.field = field;
        this.sortableFields = sortableFields;
    }
}
