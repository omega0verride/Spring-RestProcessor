package org.indritbreti.restprocessor.DynamicQueryBuilder.JPA;

import org.indritbreti.restprocessor.DynamicQueryBuilder.DynamicFilterBuilder.Filters.Filter;
import org.indritbreti.restprocessor.DynamicQueryBuilder.DynamicSortBuilder.FunctionArg;
import org.indritbreti.restprocessor.DynamicQueryBuilder.DynamicSortBuilder.MultiColumnSort;
import org.indritbreti.restprocessor.DynamicRESTController.CriteriaParameters;
import org.springframework.data.domain.Page;

import java.util.Hashtable;
import java.util.List;
import java.util.stream.Stream;

public interface DynamicQueryRepository<T> {
    Stream<T> findAllByCriteriaAsStream(CriteriaParameters cp);
    Stream<T> findAllByCriteriaAsStream(int page, int size, MultiColumnSort sortBy, List<Filter<?>> filters, Hashtable<String, FunctionArg[]> sortByFunctionArgs);
    Page<T> findAllByCriteria(CriteriaParameters cp);
    Page<T> findAllByCriteria(int page, int size, MultiColumnSort sortBy, List<Filter<?>> filters, Hashtable<String, FunctionArg[]> sortByFunctionArgs);
}
