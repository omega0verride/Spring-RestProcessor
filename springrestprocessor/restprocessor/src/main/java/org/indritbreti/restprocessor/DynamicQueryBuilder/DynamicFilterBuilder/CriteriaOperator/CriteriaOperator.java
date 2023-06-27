package org.indritbreti.restprocessor.DynamicQueryBuilder.DynamicFilterBuilder.CriteriaOperator;


public enum CriteriaOperator {

    @StandardCriteriaOperator
    EQUAL("eq"),

    @StandardCriteriaOperator
    NOT_EQUAL("neq"),

    @ComparableCriteriaOperator
    GREATER_THAN("gt"),

    @ComparableCriteriaOperator
    GREATER_THAN_OR_EQUAL("gte"),

    @ComparableCriteriaOperator
    LESS_THAN("lt"),

    @ComparableCriteriaOperator
    LESS_THAN_OR_EQUAL("lte"),

    @MultiValueCriteriaOperator
    IN("in"),

    @MultiValueCriteriaOperator
    NOT_IN("nin"),

    @RangeCriteriaOperator
    BETWEEN("btn"),

    @RangeCriteriaOperator
    NOT_BETWEEN("nbtn");

    private final String value;

    CriteriaOperator(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return this.getValue();
    }

    public static CriteriaOperator getEnum(String value) {
        for (CriteriaOperator v : values())
            if (v.getValue().equalsIgnoreCase(value)) return v;
        throw new InvalidCriteriaOperatorException(value);
    }

    public static CriteriaOperator getEnumValue(String fieldName) {
        return valueOf(fieldName);
    }
}


