package org.indritbreti.restprocessor.DynamicQueryBuilder.DynamicFilterBuilder.Filters;

import com.github.drapostolos.typeparser.TypeParser;
import com.github.drapostolos.typeparser.TypeParserException;
import org.indritbreti.restprocessor.DynamicQueryBuilder.DynamicFilterBuilder.CriteriaOperator.CriteriaOperatorValuesType;
import org.indritbreti.restprocessor.DynamicQueryBuilder.DynamicFilterBuilder.RHSColonExpression;
import org.indritbreti.restprocessor.exceptions.InvalidValueException;
import org.indritbreti.restprocessor.exceptions.InvalidValueForTypeException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FilterFactory {


    public static <T extends Number & Comparable> List<Filter<T>> getNumericFiltersFromRHSColonExpression(Class<T> class_, String fieldName, List<RHSColonExpression> rhsColonExpressions) {
        List<Filter<T>> filters = new ArrayList<>();
        if (rhsColonExpressions != null)
            for (RHSColonExpression ex : rhsColonExpressions)
                filters.add(getNumericFilterFromRHSColonExpression(class_, fieldName, ex));
        return filters;
    }

    public static <T extends Number & Comparable> Filter<T> getNumericFilterFromRHSColonExpression(Class<T> class_, String fieldName, RHSColonExpression rhsColonExpression) {
        TypeParser parser = TypeParser.newBuilder().build();
        List<T> values = new ArrayList<>();
        for (String s : rhsColonExpression.getRawValues()) {
            try {
                values.add(parser.parse(s, class_));
            } catch (TypeParserException ex) {
                throw new InvalidValueForTypeException(s, fieldName, class_.toString(), ex); // TODO try to refactor filters so that we delegate parsing to the API field constructor
            }
        }
        if (rhsColonExpression.getCriteriaOperatorValuesType() == CriteriaOperatorValuesType.Range)
            return new RangeFilter<T>(fieldName, rhsColonExpression.getCriteriaOperator(), values.get(0), values.get(1));
        else if (rhsColonExpression.getCriteriaOperatorValuesType() == CriteriaOperatorValuesType.MultiValue)
            return new MultiValueFilter<T>(fieldName, rhsColonExpression.getCriteriaOperator(), values);
        return new NumericFilter<T>(fieldName, rhsColonExpression.getCriteriaOperator(), values.get(0));
    }

    public static Filter<String> getStringFilterFromRHSColonExpression(String fieldName, RHSColonExpression rhsColonExpression) {
        if (rhsColonExpression.getCriteriaOperatorValuesType() == CriteriaOperatorValuesType.Range)
            return new RangeFilter<String>(fieldName, rhsColonExpression.getCriteriaOperator(), rhsColonExpression.getRawValues().get(0), rhsColonExpression.getRawValues().get(1));
        else if (rhsColonExpression.getCriteriaOperatorValuesType() == CriteriaOperatorValuesType.MultiValue)
            return null; // TODO should we support this?
        return new ComparableFilter<>(fieldName, rhsColonExpression.getCriteriaOperator(), rhsColonExpression.getRawValues().get(0));
    }

    public static List<Filter<String>> getStringFiltersFromRHSColonExpression(String fieldName, List<RHSColonExpression> rhsColonExpressions) {
        List<Filter<String>> filters = new ArrayList<>();
        if (rhsColonExpressions != null)
            for (RHSColonExpression ex : rhsColonExpressions)
                filters.add(getStringFilterFromRHSColonExpression(fieldName, ex));
        return filters;
    }

    //    public static Filter<Long> getLongFilterFromRHSColonExpression(String fieldName, RHSColonExpression rhsColonExpression) {
//        if (rhsColonExpression.getCriteriaOperatorValuesType() == CriteriaOperatorValuesType.Range)
//            return new RangeFilter<Long>(fieldName, rhsColonExpression.getCriteriaOperator(), Long.parseLong(rhsColonExpression.getRawValues().get(0)), Long.parseLong(rhsColonExpression.getRawValues().get(1)));
//        else if (rhsColonExpression.getCriteriaOperatorValuesType() == CriteriaOperatorValuesType.MultiValue)
//            return new MultiValueFilter<>(fieldName, rhsColonExpression.getCriteriaOperator(), rhsColonExpression.getRawValues().stream().map(Long::parseLong).collect(Collectors.toList()));
//        return new NumericFilter<Long>(fieldName, rhsColonExpression.getCriteriaOperator(), Long.parseLong(rhsColonExpression.getRawValues().get(0)));
//    }
//    public static List<Filter<Long>> getLongFiltersFromRHSColonExpression(String fieldName, List<RHSColonExpression> rhsColonExpressions) {
//        List<Filter<Long>> filters = new ArrayList<>();
//        if (rhsColonExpressions!=null)
//            for (RHSColonExpression ex:rhsColonExpressions)
//                filters.add(getLongFilterFromRHSColonExpression(fieldName, ex));
//        return filters;
//    }
//
//    public static Filter<BigDecimal> getBigDecimalFilterFromRHSColonExpression(String fieldName, RHSColonExpression rhsColonExpression) {
//        if (rhsColonExpression.getCriteriaOperatorValuesType() == CriteriaOperatorValuesType.Range)
//            return new RangeFilter<BigDecimal>(fieldName, rhsColonExpression.getCriteriaOperator(), new Double(rhsColonExpression.getRawValues().get(0)), new Double(rhsColonExpression.getRawValues().get(1)));
//        else if (rhsColonExpression.getCriteriaOperatorValuesType() == CriteriaOperatorValuesType.MultiValue)
//            return null;
//        return new NumericFilter<>(fieldName, rhsColonExpression.getCriteriaOperator(), new Double(rhsColonExpression.getRawValues().get(0)));
//    }
//    public static List<Filter<BigDecimal>> getBigDecimalFiltersFromRHSColonExpression(String fieldName, List<RHSColonExpression> rhsColonExpressions) {
//        List<Filter<BigDecimal>> filters = new ArrayList<>();
//        if (rhsColonExpressions!=null)
//            for (RHSColonExpression ex:rhsColonExpressions)
//                filters.add(getBigDecimalFilterFromRHSColonExpression(fieldName, ex));
//        return filters;
//    }
    public static Filter<LocalDateTime> getLocalDateTimeFilterFromRHSColonExpression(String fieldName, RHSColonExpression rhsColonExpression) {
        if (rhsColonExpression.getCriteriaOperatorValuesType() == CriteriaOperatorValuesType.Range)
            return new RangeFilter<LocalDateTime>(fieldName, rhsColonExpression.getCriteriaOperator(), LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(rhsColonExpression.getRawValues().get(0))), ZoneOffset.UTC), LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(rhsColonExpression.getRawValues().get(1))), ZoneOffset.UTC));
        else if (rhsColonExpression.getCriteriaOperatorValuesType() == CriteriaOperatorValuesType.MultiValue)
            return new MultiValueFilter<LocalDateTime>(fieldName, rhsColonExpression.getCriteriaOperator(), rhsColonExpression.getRawValues().stream().map(v -> LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(v)), ZoneOffset.UTC)).collect(Collectors.toList()));
        return new ComparableFilter<>(fieldName, rhsColonExpression.getCriteriaOperator(), LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(rhsColonExpression.getRawValues().get(0))), ZoneOffset.UTC));

    }

    public static List<Filter<LocalDateTime>> getLocalDateTimeFiltersFromRHSColonExpression(String fieldName, List<RHSColonExpression> rhsColonExpressions) {
        List<Filter<LocalDateTime>> filters = new ArrayList<>();
        if (rhsColonExpressions != null)
            for (RHSColonExpression ex : rhsColonExpressions)
                filters.add(getLocalDateTimeFilterFromRHSColonExpression(fieldName, ex));
        return filters;
    }


    public static JoinFilter<Long> getLongJoinFilterFromRHSColonExpression(Class<?> rootTableClass, Class<?> joinTableClass, String fieldName, RHSColonExpression rhsColonExpression) {
        return new JoinFilter<Long>(rootTableClass, joinTableClass, fieldName, rhsColonExpression.getCriteriaOperator(), Long.parseLong(rhsColonExpression.getRawValues().get(0)));
    }

    public static List<JoinFilter<Long>> getLongJoinFiltersFromRHSColonExpression(Class<?> rootTableClass, Class<?> joinTableClass, String fieldName, List<RHSColonExpression> rhsColonExpressions) {
        List<JoinFilter<Long>> filters = new ArrayList<>();
        if (rhsColonExpressions != null)
            for (RHSColonExpression ex : rhsColonExpressions)
                filters.add(getLongJoinFilterFromRHSColonExpression(rootTableClass, joinTableClass, fieldName, ex));
        return filters;
    }

    public static <T extends Enum<T>> Filter<T> getEnumFilterFromRHSColonExpression(Class<T> enumClass, String fieldName, RHSColonExpression rhsColonExpression) {
        T enumValue = null;
        try {
            enumValue = Enum.valueOf(enumClass, rhsColonExpression.getRawValues().get(0));
        } catch (IllegalArgumentException ex) {
//            throw new MethodArgumentNotValidException(new MethodParameter())
//            No enum constant gradproject.indritbreti.springscript.API.order.OrderStatus.NEW1
//            MethodArgumentNotValidException TODO
            throw new InvalidValueException("InvalidEnumValue: " + enumClass.getSimpleName() + "(" + rhsColonExpression.getRawValues().get(0) + ")");
        }

        return new Filter<>(fieldName, rhsColonExpression.getCriteriaOperator(), enumValue);
    }

    public static <T extends Enum<T>> List<? extends Filter<T>> getEnumFiltersFromRHSColonExpression(Class<T> enumClass, String fieldName, List<RHSColonExpression> rhsColonExpressions) {
        List<Filter<T>> filters = new ArrayList<>();
        if (rhsColonExpressions != null)
            for (RHSColonExpression ex : rhsColonExpressions)
                filters.add(getEnumFilterFromRHSColonExpression(enumClass, fieldName, ex));
        return filters;
    }

//    public static Filter<?> getFilter(String fieldName, Class<?> fieldType,  RHSColonExpression rhsColonExpression) {
//        if (rhsColonExpression.getCriteriaOperatorValuesType() == CriteriaOperatorValuesType.Range)
//            return new RangeFilter<String>(fieldName, rhsColonExpression.getCriteriaOperator(), rhsColonExpression.getRawValues().get(0), rhsColonExpression.getRawValues().get(1));
//        else if (rhsColonExpression.getCriteriaOperatorValuesType() == CriteriaOperatorValuesType.MultiValue)
//            return null; // TODO should we support this?
//        return new ComparableFilter<>(fieldName, rhsColonExpression.getCriteriaOperator(), rhsColonExpression.getRawValues().get(0));
//    }
//    public static List<Filter<?>> getFilters(String fieldName, List<RHSColonExpression> rhsColonExpressions){
//        List<Filter<?>> filters = new ArrayList<>();
//        if (rhsColonExpressions != null)
//            for (RHSColonExpression ex : rhsColonExpressions)
//                filters.add(getFilter(fieldName, ex));
//        return filters;
//    }
}
