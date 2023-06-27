package org.indritbreti.restprocessor.DynamicQueryBuilder.DynamicSortBuilder;

public class FunctionArg {
    int index;

    protected FunctionArg(int index) {
        this.index=index;
    }

    public int getIndex() {
        return index;
    }
}
