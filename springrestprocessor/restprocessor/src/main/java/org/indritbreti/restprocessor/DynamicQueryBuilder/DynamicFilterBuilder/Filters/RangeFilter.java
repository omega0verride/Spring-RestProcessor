package org.indritbreti.restprocessor.DynamicQueryBuilder.DynamicFilterBuilder.Filters;

import org.indritbreti.restprocessor.DynamicQueryBuilder.DynamicFilterBuilder.CriteriaOperator.RangeCriteriaOperator;
import org.indritbreti.restprocessor.DynamicQueryBuilder.DynamicFilterBuilder.CriteriaOperator.CriteriaOperator;

import java.util.Set;

public class RangeFilter<R extends Comparable> extends Filter<R> {
    private static final Set<CriteriaOperator> supportedOperators = Filter.getCriteriaOperatorSetFromAnnotationList(RangeCriteriaOperator.class);

    R rangeStart;
    R rangeEnd;

    public RangeFilter(String leftExpression, CriteriaOperator operator, R rangeStart, R rangeEnd) {
        super(leftExpression, operator, null, supportedOperators, false);
        setRangeStart(rangeStart);
        setRangeEnd(rangeEnd);
    }

    public R getRangeStart() {
        return rangeStart;
    }

    public void setRangeStart(R rangeStart) {
        this.rangeStart = rangeStart;
    }

    public R getRangeEnd() {
        return rangeEnd;
    }

    public void setRangeEnd(R rangeEnd) {
        this.rangeEnd = rangeEnd;
    }
}