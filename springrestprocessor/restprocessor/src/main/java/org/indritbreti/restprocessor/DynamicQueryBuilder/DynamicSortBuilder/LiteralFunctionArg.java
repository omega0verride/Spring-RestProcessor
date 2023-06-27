package org.indritbreti.restprocessor.DynamicQueryBuilder.DynamicSortBuilder;

public class LiteralFunctionArg extends FunctionArg {

    Object value;

    public LiteralFunctionArg(int index, Object value) {
        super(index);
        this.value=value;
    }

    public Object getValue() {
        return value;
    }
}
