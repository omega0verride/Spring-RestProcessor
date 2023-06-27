package org.indritbreti.restprocessor.DynamicQueryBuilder.DynamicFilterBuilder;

import org.indritbreti.restprocessor.DynamicQueryBuilder.DynamicFilterBuilder.CriteriaOperator.CriteriaOperator;
import org.indritbreti.restprocessor.DynamicQueryBuilder.DynamicFilterBuilder.CriteriaOperator.CriteriaOperatorValuesType;
import org.indritbreti.restprocessor.DynamicQueryBuilder.DynamicFilterBuilder.CriteriaOperator.MultiValueCriteriaOperator;
import org.indritbreti.restprocessor.DynamicQueryBuilder.DynamicFilterBuilder.CriteriaOperator.RangeCriteriaOperator;


import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RHSColonExpression {
    private final String rawString;
    private final CriteriaOperator criteriaOperator;
    private final CriteriaOperatorValuesType criteriaOperatorValuesType;

    private final List<String> rawValues;

    // TODO, I should get rid of CriteriaOperator.class.getDeclaredField(criteriaOperator.name()).getAnnotation(...
    // it is not safe, and lowers the performance for no reason
    public RHSColonExpression(String rawString) {
        this.rawString = rawString;
        if (rawString == null)
            throw new RHSColonExpressionParsingException("Raw value is NULL.");

        String[] tokens = rawString.split("(?<!\\\\):", 2);
        if (tokens.length != 2) { // if we do not find a colon, assume this is a normal EQUAL criteria price=100
            criteriaOperatorValuesType = CriteriaOperatorValuesType.SingleValue;
            criteriaOperator = CriteriaOperator.EQUAL;
            rawValues = List.of(rawString);
        } else {
            criteriaOperator = CriteriaOperator.getEnum(tokens[0]);
            try {
                if (CriteriaOperator.class.getDeclaredField(criteriaOperator.name()).getAnnotation(RangeCriteriaOperator.class) != null) {
                    rawValues = parseValues(tokens[1]);
                    if (rawValues.size() != 2)
                        throw new RHSColonExpressionParsingException("Exactly 2 values must be specified for RangeCriteriaOperator [btn]."); // TODO refactor error messages here
                    criteriaOperatorValuesType = CriteriaOperatorValuesType.Range;
                } else if (CriteriaOperator.class.getDeclaredField(criteriaOperator.name()).getAnnotation(MultiValueCriteriaOperator.class) != null) {
                    rawValues = parseValues(tokens[1]);
                    criteriaOperatorValuesType = CriteriaOperatorValuesType.MultiValue;
                } else {
                    rawValues = List.of(tokens[1]);
                    criteriaOperatorValuesType = CriteriaOperatorValuesType.SingleValue;
                }
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e); // TODO log
            }
        }
    }

    private List<String> parseValues(String val) {
        return Arrays.stream(val.split("(?<!\\\\);")).map(String::trim).collect(Collectors.toList());
    }

    public static RHSColonExpression fromString(String rawString) {
        return new RHSColonExpression(rawString);
    }

    public static class RHSColonExpressionParsingException extends RuntimeException {
        String details;

        public RHSColonExpressionParsingException() {
            this("");
        }

        public RHSColonExpressionParsingException(String details) {
            super("Could not parse RHSColon expression! " + details);
            this.details = details;
        }
    }


    public String getRawString() {
        return rawString;
    }

    public CriteriaOperator getCriteriaOperator() {
        return criteriaOperator;
    }

    public CriteriaOperatorValuesType getCriteriaOperatorValuesType() {
        return criteriaOperatorValuesType;
    }

    public List<String> getRawValues() {
        return rawValues;
    }
}

