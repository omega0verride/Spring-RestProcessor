package org.indritbreti.restprocessor.DynamicQueryBuilder.DynamicFilterBuilder.Filters;

import org.indritbreti.restprocessor.DynamicQueryBuilder.DynamicFilterBuilder.CriteriaOperator.ComparableCriteriaOperator;
import org.indritbreti.restprocessor.DynamicQueryBuilder.DynamicFilterBuilder.CriteriaOperator.CriteriaOperator;

import java.util.Set;

public class ComparableFilter<R extends Comparable> extends Filter<R> {
    private static final Set<CriteriaOperator> supportedOperators = Filter.getCriteriaOperatorSetFromAnnotationList(ComparableCriteriaOperator.class);

    public ComparableFilter(String leftExpression, CriteriaOperator operator, R rightExpression) {
        super(leftExpression, operator, rightExpression, supportedOperators);
    }
}