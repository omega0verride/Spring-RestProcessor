package org.indritbreti.restprocessor.DynamicQueryBuilder.DynamicFilterBuilder.Filters;

import org.indritbreti.restprocessor.DynamicQueryBuilder.DynamicFilterBuilder.CriteriaOperator.CriteriaOperator;
import org.indritbreti.restprocessor.DynamicQueryBuilder.DynamicFilterBuilder.CriteriaOperator.StandardCriteriaOperator;
import org.indritbreti.restprocessor.DynamicQueryBuilder.DynamicFilterBuilder.CriteriaOperator.UnsupportedCriteriaOperatorException;
import org.indritbreti.restprocessor.DynamicQueryBuilder.DynamicQueryBuilderUtils;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Filter<R> implements Cloneable {
    private final Set<CriteriaOperator> supportedOperators = getCriteriaOperatorSetFromAnnotationList(StandardCriteriaOperator.class);
    // a set of the operators that this filter supports, by default all types of filters support equals and not equals
    // those are internal to the filter itself, i.e.: comparable filters support gt, gte, lt etc.
    // if you want to limit the operators for a specific field you need to use RHSColonExpression#allowedOperators TODO
    private String leftExpression;
    private String[] leftExpressionPaths;
    CriteriaOperator operator;

    R rightExpression;

    public Filter(String leftExpression, CriteriaOperator operator, R rightExpression, Set<CriteriaOperator> allowedOperators, boolean useStandardCriteriaOperators) {
        if (!useStandardCriteriaOperators)
            supportedOperators.clear();
        setSupportedOperators(allowedOperators);
        setLeftExpression(leftExpression);
        setOperator(operator);
        setRightExpression(rightExpression);
    }

    public Filter(String leftExpression, CriteriaOperator operator, R rightExpression, Set<CriteriaOperator> allowedOperators) {
        this(leftExpression, operator, rightExpression, allowedOperators, true);
    }

    public Filter(String leftExpression, CriteriaOperator operator, R rightExpression) {
        this(leftExpression, operator, rightExpression, null);
    }

    // must be overwritten on some cases
    public Expression getLeftExpression(Root<?> root, CriteriaBuilder criteriaBuilder) {
        return buildLeftExpressionFromPaths(root);
    }

    public void setOperator(CriteriaOperator operator) {
        if (!supportedOperators.contains(operator))
            throw new UnsupportedCriteriaOperatorException(operator, supportedOperators);
        this.operator = operator;
    }

    private void setSupportedOperators(Set<CriteriaOperator> supportedOperators) {
        if (supportedOperators != null)
            this.supportedOperators.addAll(supportedOperators);
    }

    @Override
    public Filter<R> clone() {
        try {
            Filter clone = (Filter) super.clone();
            // TODO: copy mutable state here, so the clone can't change the internals of the original
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @SafeVarargs
    static Set<CriteriaOperator> getCriteriaOperatorSetFromAnnotationList(Class<? extends Annotation>... ICriteriaOperatorAnnotations) {
        return Arrays.stream(CriteriaOperator.class.getDeclaredFields()).filter(f -> {
            for (Class<? extends Annotation> c : ICriteriaOperatorAnnotations)
                if (f.isAnnotationPresent(c))
                    return true;
            return false;
        }).map(f -> CriteriaOperator.getEnumValue(f.getName())).collect(Collectors.toSet());
    }

    public final void setLeftExpression(String leftExpression) {
        this.leftExpression = leftExpression;
        leftExpressionPaths = leftExpression.split("\\.");
        if (leftExpressionPaths.length == 0 || leftExpression.trim().length() == 0)
            throw new InvalidFilterException(this);
    }

    public Expression<?> buildLeftExpressionFromPaths(Root<?> root) {
        return DynamicQueryBuilderUtils.buildExpressionFromPersistencePaths(root, List.of(getLeftExpressionPaths()));
    }

    public Set<CriteriaOperator> getSupportedOperators() {
        return supportedOperators;
    }

    public String getLeftExpression() {
        return leftExpression;
    }

    public String[] getLeftExpressionPaths() {
        return leftExpressionPaths;
    }

    public void setLeftExpressionPaths(String[] leftExpressionPaths) {
        this.leftExpressionPaths = leftExpressionPaths;
    }

    public CriteriaOperator getOperator() {
        return operator;
    }

    public R getRightExpression() {
        return rightExpression;
    }

    public void setRightExpression(R rightExpression) {
        this.rightExpression = rightExpression;
    }


}