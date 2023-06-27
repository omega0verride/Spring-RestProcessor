package org.indritbreti.restprocessor.DynamicQueryBuilder.DynamicFilterBuilder.Filters;

import org.indritbreti.restprocessor.DynamicQueryBuilder.DynamicFilterBuilder.CriteriaOperator.CriteriaOperator;

public class FullTextSearchFilter extends FunctionFilter<Boolean, String> {
    public FullTextSearchFilter(String searchQuery) {
        super("fts", CriteriaOperator.EQUAL, true, Boolean.class, searchQuery);
    }
}