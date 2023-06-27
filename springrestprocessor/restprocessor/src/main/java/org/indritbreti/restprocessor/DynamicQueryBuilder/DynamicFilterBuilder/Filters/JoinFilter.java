package org.indritbreti.restprocessor.DynamicQueryBuilder.DynamicFilterBuilder.Filters;

import org.indritbreti.restprocessor.DynamicQueryBuilder.DynamicFilterBuilder.CriteriaOperator.CriteriaOperator;
import jakarta.persistence.Table;
import jakarta.persistence.criteria.*;

public class JoinFilter<R> extends Filter<R> {

    private Class Table1;
    private Class Table2;

    private final String joinTableName;

    public JoinFilter(Class<?> rootTableClass, Class<?> joinTableClass, String leftExpression, CriteriaOperator operator, R rightExpression) {
        super(leftExpression, operator, rightExpression);
        setOperator(operator);
        this.joinTableName = joinTableClass.getAnnotation(Table.class).name();
    }

    @Override
    public Expression<?> getLeftExpression(Root<?> root, CriteriaBuilder criteriaBuilder) {
        return buildFilter(root);
    }

    private Expression<?> buildFilter(Root<?> root) {
        Join<?, ?> category = root.join(joinTableName, JoinType.LEFT);
        return category.get(getLeftExpression());
    }

    public String getJoinTableName() {
        return joinTableName;
    }
}