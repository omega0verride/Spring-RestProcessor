package org.indritbreti.restprocessor;

import org.indritbreti.restprocessor.DynamicQueryBuilder.DynamicSortBuilder.SortOrder;

public interface IRestFieldDetails {
    String getUniqueAPIName();
    SortOrder getDefaultSortOrder();
    boolean isSortable();
    boolean isFilterable();
}
