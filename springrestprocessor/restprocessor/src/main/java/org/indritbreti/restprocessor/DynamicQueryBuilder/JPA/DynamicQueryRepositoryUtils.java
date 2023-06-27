package org.indritbreti.restprocessor.DynamicQueryBuilder.JPA;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.indritbreti.restprocessor.DynamicQueryBuilder.DynamicFilterBuilder.Filters.Filter;
import org.indritbreti.restprocessor.DynamicQueryBuilder.DynamicFilterBuilder.PredicateBuilder;
import org.indritbreti.restprocessor.DynamicQueryBuilder.DynamicSortBuilder.FunctionArg;
import org.indritbreti.restprocessor.DynamicQueryBuilder.DynamicSortBuilder.MultiColumnSort;
import org.indritbreti.restprocessor.DynamicRESTController.CriteriaParameters;
import org.indritbreti.restprocessor.DynamicQueryBuilder.DynamicQueryBuilderUtils;
import org.indritbreti.restprocessor.IRestFieldDetails;
import org.indritbreti.restprocessor.exceptions.BaseException;
import org.indritbreti.restprocessor.exceptions.UnknownException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Hashtable;
import java.util.List;
import java.util.stream.Stream;

public class DynamicQueryRepositoryUtils {

    public static <ENTITY_TYPE> Stream findAllByCriteriaAsStream(Class<ENTITY_TYPE> entityClass, EntityManager entityManager, Hashtable<String, IRestFieldDetails> sortableFieldDetails, CriteriaParameters cp) {
        return findAllByCriteriaAsStream(entityClass, entityManager, cp.getSortBy(), cp.getFilters(), sortableFieldDetails, cp.getSortByFunctionArgs());
    }

    public static <ENTITY_TYPE> Stream findAllByCriteriaAsStream(Class<ENTITY_TYPE> entityClass, EntityManager entityManager, MultiColumnSort sortBy, List<Filter<?>> filters, Hashtable<String, IRestFieldDetails> sortableFieldDetails, Hashtable<String, FunctionArg[]> sortByFunctionArgs) {
        Query query = buildQuery(entityClass, entityManager, sortBy, filters, sortableFieldDetails, sortByFunctionArgs);
        query.setFirstResult(0);
        return query.getResultStream();
    }

    public static <ENTITY_TYPE> Page<ENTITY_TYPE> findAllByCriteria(Class<ENTITY_TYPE> entityClass, EntityManager entityManager, Hashtable<String, IRestFieldDetails> sortableFieldDetails, CriteriaParameters cp) {
        return findAllByCriteria(entityClass, entityManager, cp.getPageNumber(), cp.getPageSize(), cp.getSortBy(), cp.getFilters(), sortableFieldDetails, cp.getSortByFunctionArgs());
    }

    public static <ENTITY_TYPE> Page<ENTITY_TYPE> findAllByCriteria(Class<ENTITY_TYPE> entityClass, EntityManager entityManager, int page, int size, MultiColumnSort sortBy, List<Filter<?>> filters, Hashtable<String, IRestFieldDetails> sortableFieldDetails, Hashtable<String, FunctionArg[]> sortByFunctionArgs) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Query query = buildQuery(entityClass, entityManager, sortBy, filters, sortableFieldDetails, sortByFunctionArgs);
            query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
            query.setMaxResults(pageable.getPageSize());
            List<ENTITY_TYPE> resultList = query.getResultList();

            CriteriaQuery<Long> countCriteriaQuery = buildCountCriteriaQuery(entityClass, entityManager.getCriteriaBuilder(), filters);
            Long totalCNT = entityManager.createQuery(countCriteriaQuery).getSingleResult();

            return new PageImpl<>(resultList, pageable, totalCNT);
        } catch (Exception ex) {
            if (ex instanceof BaseException ex_)
                throw ex_;
            throw new UnknownException("Something went wrong with dynamic query repository! Reach out to support team.", ex);
        }
    }

    private static <ENTITY_TYPE> Query buildQuery(Class<ENTITY_TYPE> entityClass, EntityManager entityManager, MultiColumnSort sortBy, List<Filter<?>> filters, Hashtable<String, IRestFieldDetails> sortableFieldDetails, Hashtable<String, FunctionArg[]> sortByFunctionArgs) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ENTITY_TYPE> criteriaQuery = buildSelectCriteriaQuery(entityClass, criteriaBuilder, sortBy, filters, sortableFieldDetails, sortByFunctionArgs);
        return entityManager.createQuery(criteriaQuery);
    }

    protected static <ENTITY_TYPE> CriteriaQuery<ENTITY_TYPE> buildSelectCriteriaQuery(Class<ENTITY_TYPE> entityClass, CriteriaBuilder criteriaBuilder, MultiColumnSort sortBy, List<Filter<?>> filters, Hashtable<String, IRestFieldDetails> sortableFieldDetails, Hashtable<String, FunctionArg[]> sortByFunctionArgs) {
        return buildCriteriaQuery(entityClass, criteriaBuilder, false, sortBy, filters, sortableFieldDetails, sortByFunctionArgs);
    }

    protected static <PRIMARY_KEY_TYPE> CriteriaQuery<PRIMARY_KEY_TYPE> buildCountCriteriaQuery(Class<?> entityClass, CriteriaBuilder criteriaBuilder, List<Filter<?>> filters) {
        return buildCriteriaQuery(entityClass, criteriaBuilder, true, null, filters, null, null);
    }

    protected static <ENTITY_TYPE> CriteriaQuery buildCriteriaQuery(Class<ENTITY_TYPE> entityClass, CriteriaBuilder criteriaBuilder, boolean isCountQuery, MultiColumnSort sortBy, List<Filter<?>> filters, Hashtable<String, IRestFieldDetails> sortableFieldDetails, Hashtable<String, FunctionArg[]> sortByFunctionArgs) {
        CriteriaQuery criteriaQuery;
        if (isCountQuery)
            criteriaQuery = criteriaBuilder.createQuery(Long.class);
        else
            criteriaQuery = criteriaBuilder.createQuery(entityClass);

        Root<ENTITY_TYPE> root = criteriaQuery.from(entityClass);
        criteriaQuery.where(PredicateBuilder.predicatesFromFilters(filters, criteriaBuilder, root));
        if (isCountQuery) // count query
            criteriaQuery.select(criteriaBuilder.count(root));
        else if (sortBy != null) // not a count query, apply sort
            criteriaQuery.orderBy(DynamicQueryBuilderUtils.getOrdersFromSortDetails(criteriaBuilder, root, sortBy.getSortDetails(), sortableFieldDetails, sortByFunctionArgs));
        return criteriaQuery;
    }
}
