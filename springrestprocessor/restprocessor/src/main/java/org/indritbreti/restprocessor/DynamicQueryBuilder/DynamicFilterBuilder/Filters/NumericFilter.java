package org.indritbreti.restprocessor.DynamicQueryBuilder.DynamicFilterBuilder.Filters;

import org.indritbreti.restprocessor.DynamicQueryBuilder.DynamicFilterBuilder.CriteriaOperator.CriteriaOperator;

public class NumericFilter<R extends Number & Comparable> extends ComparableFilter<R> {
    public NumericFilter(String leftExpression, CriteriaOperator operator, R rightExpression) {
        super(leftExpression, operator, rightExpression);
    }
}