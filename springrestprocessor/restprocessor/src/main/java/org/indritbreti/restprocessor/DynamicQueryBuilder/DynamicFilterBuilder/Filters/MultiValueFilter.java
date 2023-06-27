package org.indritbreti.restprocessor.DynamicQueryBuilder.DynamicFilterBuilder.Filters;

import org.indritbreti.restprocessor.DynamicQueryBuilder.DynamicFilterBuilder.CriteriaOperator.CriteriaOperator;
import org.indritbreti.restprocessor.DynamicQueryBuilder.DynamicFilterBuilder.CriteriaOperator.MultiValueCriteriaOperator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MultiValueFilter<R extends Comparable> extends Filter<R> {
    private static final Set<CriteriaOperator> supportedOperators = Filter.getCriteriaOperatorSetFromAnnotationList(MultiValueCriteriaOperator.class);

    List<R> values = new ArrayList<>();

    public MultiValueFilter(String leftExpression, CriteriaOperator operator, List<R> values) {
        super(leftExpression, operator, null, supportedOperators, false);
        setValues(values);
    }

    public void setValues(List<R> values) {
        this.values.clear();
        this.values.addAll(values);
    }

    public List<R> getValues() {
        return values;
    }
}