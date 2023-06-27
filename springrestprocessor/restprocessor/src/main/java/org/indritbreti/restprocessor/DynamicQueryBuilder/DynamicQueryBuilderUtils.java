package org.indritbreti.restprocessor.DynamicQueryBuilder;

import jakarta.persistence.criteria.*;
import org.indritbreti.restprocessor.DynamicQueryBuilder.DynamicSortBuilder.FunctionArg;
import org.indritbreti.restprocessor.DynamicQueryBuilder.DynamicSortBuilder.SortByFunction;
import org.indritbreti.restprocessor.DynamicQueryBuilder.DynamicSortBuilder.SortOrder;
import org.indritbreti.restprocessor.PersistencePath;
import org.indritbreti.restprocessor.FieldDetails;
import org.indritbreti.restprocessor.IRestFieldDetails;
import org.indritbreti.restprocessor.exceptions.InvalidSortFieldException;

import java.util.*;
import java.util.stream.Collectors;

public class DynamicQueryBuilderUtils {

    public static List<Order> getOrdersFromSortDetails(CriteriaBuilder criteriaBuilder, Root<?> root, LinkedHashMap<String, SortOrder> sortDetails, Hashtable<String, IRestFieldDetails> sortableFieldDetails, Hashtable<String, FunctionArg[]> sortByFunctionArgs) {
        List<Order> orderList = new ArrayList<>();
        if (sortDetails == null)
            sortDetails = new LinkedHashMap<>();
        if (sortableFieldDetails == null)
            sortableFieldDetails = new Hashtable<>();
        for (Map.Entry<String, SortOrder> sortDetail : sortDetails.entrySet()) {
            if (!sortableFieldDetails.containsKey(sortDetail.getKey()))
                throw new InvalidSortFieldException(sortDetail.getKey(), sortableFieldDetails.keySet());
            Expression<?> expression = null;
            IRestFieldDetails restFieldDetails = sortableFieldDetails.get(sortDetail.getKey());
            if (!restFieldDetails.isSortable())
                throw new InvalidSortFieldException(sortDetail.getKey(), sortableFieldDetails.keySet());
            if (restFieldDetails instanceof FieldDetails restFieldDetails_) {
                expression = buildExpressionFromPersistencePaths(root, restFieldDetails_.getPersistencePaths().stream().map(PersistencePath::getPath).collect(Collectors.toList()));
            } else if (restFieldDetails instanceof SortByFunction<?> restFieldDetails_) {
                expression = restFieldDetails_.buildFunction(root, criteriaBuilder, sortByFunctionArgs);
            }
            if (expression != null) {
                SortOrder sortOrder_ = sortDetail.getValue();
                if (sortOrder_ == null)
                    sortOrder_ = restFieldDetails.getDefaultSortOrder();
                if (sortOrder_ == SortOrder.DESC)
                    orderList.add(criteriaBuilder.desc(expression));
                else
                    orderList.add(criteriaBuilder.asc(expression));
            }
        }
        return orderList;
    }

    public static Expression<?> buildExpressionFromPersistencePaths(Root<?> root, List<String> persistencePaths) {
        Path<?> path = root.get(persistencePaths.get(0));
        for (int i = 1; i < persistencePaths.size(); i++) {
            path = path.get(persistencePaths.get(i));
        }
        return path;
    }
}
