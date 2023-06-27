package org.indritbreti.restprocessor.DynamicQueryBuilder.DynamicFilterBuilder.CriteriaOperator;

import java.util.Set;

public class UnsupportedCriteriaOperatorException extends RuntimeException {
    Set<CriteriaOperator> supportedCriteriaOperators;
    CriteriaOperator op;

    public UnsupportedCriteriaOperatorException(CriteriaOperator op, Set<CriteriaOperator> supportedCriteriaOperators) {
        super("Invalid Criteria Operator! Operator: '" + op + "'" + " is not supported. Supported Operators: " + supportedCriteriaOperators);
        this.op = op;
        this.supportedCriteriaOperators = supportedCriteriaOperators;
    }
}
