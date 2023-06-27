package org.indritbreti.restprocessor.DynamicRESTController;


import org.indritbreti.restprocessor.DynamicQueryBuilder.DynamicFilterBuilder.Filters.Filter;
import org.indritbreti.restprocessor.DynamicQueryBuilder.DynamicSortBuilder.FunctionArg;
import org.indritbreti.restprocessor.DynamicQueryBuilder.DynamicSortBuilder.MultiColumnSort;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class CriteriaParameters {
    int pageNumber = 0;
    int pageSize = 30; // TODO get defaults
    MultiColumnSort sortBy;
    List<Filter<?>> filters = new ArrayList<>();

    Hashtable<String, List<FunctionArg>> sortByFunctionArgs = new Hashtable<>();

    public CriteriaParameters(int pageNumber, int pageSize, MultiColumnSort sortBy, List<Filter<?>> filters) {
        this(pageNumber, pageSize, sortBy, filters, null);
    }

    public CriteriaParameters(int pageNumber, int pageSize, MultiColumnSort sortBy, List<Filter<?>> filters, Hashtable<String, List<FunctionArg>> sortByFunctionArgs) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.sortBy = sortBy;
        if (filters != null)
            this.filters = filters;
        if (sortByFunctionArgs != null)
            this.sortByFunctionArgs = sortByFunctionArgs;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public MultiColumnSort getSortBy() {
        return sortBy;
    }

    public void setSortBy(MultiColumnSort sortBy) {
        this.sortBy = sortBy;
    }

    public List<Filter<?>> getFilters() {
        return filters;
    }

    public void setFilters(List<Filter<?>> filters) {
        this.filters = filters;
    }

    public Hashtable<String, List<FunctionArg>> getSortByFunctionArgsAsList() {
        return sortByFunctionArgs;
    }

    public Hashtable<String, FunctionArg[]> getSortByFunctionArgs() {
        Hashtable<String, FunctionArg[]> args_ = new Hashtable<>();
        for (Map.Entry<String, List<FunctionArg>> entry : sortByFunctionArgs.entrySet()) {
            args_.put(entry.getKey(), entry.getValue().toArray(new FunctionArg[0]));
        }
        return args_;
    }

    public void setSortByFunctionArgs(Hashtable<String, List<FunctionArg>> sortByFunctionArgs) {
        this.sortByFunctionArgs = sortByFunctionArgs;
    }

    public void addSortByFunctionArg(String uniqueApiName, FunctionArg functionArg) {
        if (!sortByFunctionArgs.containsKey(uniqueApiName))
            sortByFunctionArgs.put(uniqueApiName, new ArrayList<>());
        sortByFunctionArgs.get(uniqueApiName).add(functionArg);
    }

    public void removeSortByFunctionArg(String uniqueApiName, int index) {
        if (sortByFunctionArgs.containsKey(uniqueApiName))
            try {
                sortByFunctionArgs.get(uniqueApiName).remove(index);
            } catch (Exception ignored) {
            }
    }

    public void removeSortByFunctionArg(String uniqueApiName, FunctionArg functionArg) {
        if (sortByFunctionArgs.containsKey(uniqueApiName))
            try {
                sortByFunctionArgs.get(uniqueApiName).remove(functionArg);
            } catch (Exception ignored) {
            }
    }

    public void addFilter(Filter<?> filter){
        filters.add(filter);
    }

    public void removeFilter(Filter<?> filter){
        filters.remove(filter);
    }

    public void removeFilter(int index){
        filters.remove(index);
    }
}
