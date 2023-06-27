package org.indritbreti.restprocessor.DynamicQueryBuilder.DynamicFilterBuilder;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.indritbreti.restprocessor.DynamicQueryBuilder.DynamicFilterBuilder.Filters.*;

import java.util.List;

public class PredicateBuilder {

    // the main point to notice here is why did I have to create a predicate builder class?
    // I could create a toPredicate() method and override it for each filter imp but that is not the Filter class responsibility
    // the filters can be used in some other scenario where predicates are irrelevant

    public static Predicate predicateFromFilter(CriteriaBuilder criteriaBuilder, Root<?> root, Filter<?> filter) {
        if (filter.getRightExpression() != null) {
            switch (filter.getOperator()) {
                case EQUAL -> {
                    return criteriaBuilder.equal(filter.getLeftExpression(root, criteriaBuilder), filter.getRightExpression());
                }
                case NOT_EQUAL -> {
                    return criteriaBuilder.notEqual(filter.getLeftExpression(root, criteriaBuilder), filter.getRightExpression());
                }
            }
        }
        if (filter instanceof RangeFilter<? extends Comparable> rangeFilter) {
            switch (rangeFilter.getOperator()) {
                case BETWEEN -> {
                    return criteriaBuilder.between(rangeFilter.getLeftExpression(root, criteriaBuilder), rangeFilter.getRangeStart(), rangeFilter.getRangeEnd());
                }
                case NOT_BETWEEN -> {
                    return criteriaBuilder.not(criteriaBuilder.between(rangeFilter.getLeftExpression(root, criteriaBuilder), rangeFilter.getRangeStart(), rangeFilter.getRangeEnd()));
                }
            }
        }
        if (filter instanceof MultiValueFilter<? extends Comparable> multiValueFilter) {
            switch (multiValueFilter.getOperator()) {
                case IN -> {
                    return multiValueFilter.getLeftExpression(root, criteriaBuilder).in(multiValueFilter.getValues());
                }
                case NOT_IN -> {
                    return multiValueFilter.getLeftExpression(root, criteriaBuilder).in(multiValueFilter.getValues()).not();
                }
            }
        }
        if (filter instanceof ComparableFilter<?> comparableFilter) {
            if (comparableFilter instanceof NumericFilter<?> numericFilter) {
                switch (filter.getOperator()) {
                    case GREATER_THAN -> {
                        return criteriaBuilder.gt(numericFilter.getLeftExpression(root, criteriaBuilder), numericFilter.getRightExpression());
                    }
                    case GREATER_THAN_OR_EQUAL -> {
                        return criteriaBuilder.ge(numericFilter.getLeftExpression(root, criteriaBuilder), numericFilter.getRightExpression());
                    }
                    case LESS_THAN -> {
                        return criteriaBuilder.lt(numericFilter.getLeftExpression(root, criteriaBuilder), numericFilter.getRightExpression());
                    }
                    case LESS_THAN_OR_EQUAL -> {
                        return criteriaBuilder.le(numericFilter.getLeftExpression(root, criteriaBuilder), numericFilter.getRightExpression());
                    }
                }
            } else {
                switch (comparableFilter.getOperator()) {
                    case GREATER_THAN -> {
                        return criteriaBuilder.greaterThan(comparableFilter.getLeftExpression(root, criteriaBuilder), comparableFilter.getRightExpression());
                    }
                    case GREATER_THAN_OR_EQUAL -> {
                        return criteriaBuilder.greaterThanOrEqualTo(comparableFilter.getLeftExpression(root, criteriaBuilder), comparableFilter.getRightExpression());
                    }
                    case LESS_THAN -> {
                        return criteriaBuilder.lessThan(comparableFilter.getLeftExpression(root, criteriaBuilder), comparableFilter.getRightExpression());
                    }
                    case LESS_THAN_OR_EQUAL -> {
                        return criteriaBuilder.lessThanOrEqualTo(comparableFilter.getLeftExpression(root, criteriaBuilder), comparableFilter.getRightExpression());
                    }
                }
            }
        }
        throw new RuntimeException("Filter Operator Not Supported! Operator: " + filter.getOperator());
    }

    public static Predicate[] predicatesFromFilters(List<Filter<?>> filters, CriteriaBuilder criteriaBuilder, Root root) {
        Predicate[] predicates = new Predicate[filters.size()];
        for (int i = 0; i < filters.size(); i++)
            predicates[i] = predicateFromFilter(criteriaBuilder, root, filters.get(i));
        return predicates;
    }
}
