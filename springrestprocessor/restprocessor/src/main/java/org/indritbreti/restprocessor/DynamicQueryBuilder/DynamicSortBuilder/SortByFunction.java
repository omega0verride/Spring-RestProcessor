package org.indritbreti.restprocessor.DynamicQueryBuilder.DynamicSortBuilder;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;
import org.indritbreti.restprocessor.DynamicQueryBuilder.DynamicQueryBuilderUtils;
import org.indritbreti.restprocessor.IRestFieldDetails;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class SortByFunction<RETURN_TYPE> implements IRestFieldDetails, Cloneable, Serializable {
    private String uniqueAPIName;
    private String functionName;
    private int minRequiredArgs = 0;
    private Class<?> returnType;
    private List<FunctionArg> staticFunctionArgs;
    private SortOrder defaultSortOrder = SortOrder.ASC;

    public SortByFunction() {
    }

    public SortByFunction(String functionName, Class<?> returnType, String uniqueAPIName) {
        this(functionName, returnType, uniqueAPIName, 0);
    }

    public SortByFunction(String functionName, Class<?> returnType, String uniqueAPIName, int minRequiredArgs) {
        this(functionName, returnType, uniqueAPIName, 0, null);
    }

    public SortByFunction(String functionName, Class<?> returnType, String uniqueAPIName, int minRequiredArgs, SortOrder defaultSortOrder, FunctionArg ... staticFunctionArgs) {
        setUniqueAPIName(uniqueAPIName);
        setFunctionName(functionName);
        setReturnType(returnType);
        setMinRequiredArgs(minRequiredArgs);
        this.staticFunctionArgs = List.of(staticFunctionArgs);
        if (defaultSortOrder != null)
            this.defaultSortOrder = defaultSortOrder;
    }

    public Expression<?> buildFunction(Root<?> root, CriteriaBuilder criteriaBuilder, Hashtable<String, FunctionArg[]> sortByFunctionArgs) {
        FunctionArg[] args = sortByFunctionArgs.getOrDefault(getUniqueAPIName(), new FunctionArg[0]);
        return criteriaBuilder.function(getFunctionName(), getReturnType(), buildArgs(root, criteriaBuilder, List.of(args)));
    }

    @Override
    public String getUniqueAPIName() {
        return uniqueAPIName;
    }

    @Override
    public SortOrder getDefaultSortOrder() {
        return defaultSortOrder;
    }

    @Override
    public boolean isSortable() {
        return true;
    }

    @Override
    public boolean isFilterable() {
        return false;
    }

    public String getFunctionName() {
        return this.functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public Class<?> getReturnType() {
        return returnType;
    }

    public void setReturnType(Class<?> returnType) {
        this.returnType = returnType;
    }

    private Expression<?>[] buildArgs(Root<?> root, CriteriaBuilder criteriaBuilder, List<FunctionArg> args_) {
        List<FunctionArg> argsList = new ArrayList<>(args_);
        if (staticFunctionArgs != null)
            argsList.addAll(staticFunctionArgs);
        if (argsList.size() < minRequiredArgs)
            throw new SortByFunctionArgumentsException(getFunctionName(), getMinRequiredArgs(), argsList.size());
        argsList = argsList.stream().sorted(Comparator.comparingInt(FunctionArg::getIndex)).collect(Collectors.toList());

        Expression<?>[] args = new Expression[argsList.size()];
        Set<Integer> tempSet = new HashSet<>();
        int i = 0;
        for (FunctionArg functionArg : argsList) {
            if (functionArg.getIndex() < 0)
                throw new SortByFunctionArgumentsException(getFunctionName(), functionArg.getIndex());
            if (tempSet.contains(functionArg.getIndex()))
                throw new SortByFunctionArgumentsException(getFunctionName(), functionArg.getIndex(), true);
            if (functionArg.getIndex() != i)
                throw new SortByFunctionArgumentsException(getFunctionName(), functionArg.getIndex(), i, true);
            tempSet.add(functionArg.getIndex());
            if (functionArg instanceof LiteralFunctionArg functionArg_) {
                args[i] = criteriaBuilder.literal(functionArg_.getValue());
                i++;
            } else if (functionArg instanceof PathFunctionArg functionArg_) {
                args[i] = DynamicQueryBuilderUtils.buildExpressionFromPersistencePaths(root, functionArg_.getPaths());
                i++;
            }
        }
        return args;
    }

    public void setUniqueAPIName(String uniqueAPIName) {
        this.uniqueAPIName = uniqueAPIName;
    }

    public void setDefaultSortOrder(SortOrder defaultSortOrder) {
        this.defaultSortOrder = defaultSortOrder;
    }

    public int getMinRequiredArgs() {
        return minRequiredArgs;
    }

    public void setMinRequiredArgs(int minRequiredArgs) {
        this.minRequiredArgs = minRequiredArgs;
    }

    public List<FunctionArg> getStaticFunctionArgs() {
        return staticFunctionArgs;
    }

    @Override
    public SortByFunction<RETURN_TYPE> clone() {
        return new SortByFunction<RETURN_TYPE>(getFunctionName(), getReturnType(), getUniqueAPIName(), getMinRequiredArgs(), getDefaultSortOrder(), getStaticFunctionArgs().toArray(new FunctionArg[0]));
    }
}
