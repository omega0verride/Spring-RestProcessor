package org.indritbreti.restprocessor.DynamicQueryBuilder.DynamicFilterBuilder.CriteriaOperator;

import org.indritbreti.restprocessor.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class InvalidCriteriaOperatorException extends BaseException {
    String criteriaOperatorValue;

    public InvalidCriteriaOperatorException(String criteriaOperatorValue) {
        super(HttpStatus.BAD_REQUEST, "Invalid Criteria Operator! Could not map criteria operator: '" + criteriaOperatorValue + "'");
        this.criteriaOperatorValue = criteriaOperatorValue;
    }
}
