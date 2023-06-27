package org.indritbreti.restprocessor.DynamicQueryBuilder.DynamicFilterBuilder.Filters;

import org.indritbreti.restprocessor.DynamicQueryBuilder.DynamicFilterBuilder.CriteriaOperator.CriteriaOperator;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;

import java.util.List;


public class FunctionFilter<RETURN_TYPE, ARGS_TYPE> extends Filter<RETURN_TYPE> {
    // used for function calls

    //   List<R> values;
    List<ARGS_TYPE> args;

    Class<?> returnType;

    @SafeVarargs
    public FunctionFilter(String functionName, CriteriaOperator operator, RETURN_TYPE rightExpression, Class<?> returnType, ARGS_TYPE... args) {
        super(functionName, operator, rightExpression);
        setReturnType(returnType);
        setArgs(args == null ? List.of() : List.of(args));
    }

    @Override
    public Expression<?> getLeftExpression(Root<?> root, CriteriaBuilder criteriaBuilder) {
        return buildFunction(criteriaBuilder);
    }

    private Expression<?> buildFunction(CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.function(getLeftExpression(), getReturnType(), buildArgs(criteriaBuilder));
    }

    private Expression<?>[] buildArgs(CriteriaBuilder criteriaBuilder) {
        Expression<?>[] args = new Expression[getArgs().size()];
        for (int i = 0; i < getArgs().size(); i++)
            args[i] = criteriaBuilder.literal(getArgs().get(i));
        return args;
    }

    public String getFunctionName() {
        return getLeftExpression();
    }

    public void setFunctionName(String functionName) {
        setLeftExpression(functionName);
    }

    public List<ARGS_TYPE> getArgs() {
        return args;
    }

    public void setArgs(List<ARGS_TYPE> args) {
        this.args = args;
    }

    public Class<?> getReturnType() {
        return returnType;
    }

    public void setReturnType(Class<?> returnType) {
        this.returnType = returnType;
    }
}