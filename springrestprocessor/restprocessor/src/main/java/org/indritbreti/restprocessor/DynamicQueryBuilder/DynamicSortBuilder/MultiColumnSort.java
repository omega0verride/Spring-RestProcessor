package org.indritbreti.restprocessor.DynamicQueryBuilder.DynamicSortBuilder;


import java.util.LinkedHashMap;
import java.util.List;

public class MultiColumnSort {

    // sortBy=+name;-price;...
    private static final String MultiValueDelimiter = ";";
    private static final char ASC_ORDER_SYMBOL = '+';
    private static final char DESC_ORDER_SYMBOL = '-';
    private LinkedHashMap<String, SortOrder> sortDetails = new LinkedHashMap<>();
    boolean usesDefaultSort = false;

    public MultiColumnSort(String expression) {
        sortDetails = parseSortDetailsFromExpression(expression);
    }

    public MultiColumnSort(List<String> expressions) {
        for (String expression : expressions)
            sortDetails.putAll(parseSortDetailsFromExpression(expression));
    }

    private LinkedHashMap<String, SortOrder> parseSortDetailsFromExpression(String expression) {
        LinkedHashMap<String, SortOrder> sortDetails_ = new LinkedHashMap<>();
        String[] values = expression.split("(?<!\\\\)"+MultiValueDelimiter);
        for (String f : values) {
            f = f.trim();
            if (f.charAt(0) == DESC_ORDER_SYMBOL)
                sortDetails_.put(f.substring(1), SortOrder.DESC);
            else if (f.charAt(0) == ASC_ORDER_SYMBOL)
                sortDetails_.put(f.substring(1), SortOrder.ASC);
            else {
                usesDefaultSort = true;
                sortDetails_.put(f, null);
            }
        }
        return sortDetails_;
    }

    public static char getAscOrderSymbol() {
        return ASC_ORDER_SYMBOL;
    }

    public static char getDescOrderSymbol() {
        return DESC_ORDER_SYMBOL;
    }

    public LinkedHashMap<String, SortOrder> getSortDetails() {
        return new LinkedHashMap<>(sortDetails);
    }
    public static String getMultiValueDelimiter() {
        return MultiValueDelimiter;
    }
}


