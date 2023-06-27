package org.indritbreti.restprocessor.DynamicQueryBuilder.DynamicSortBuilder;

public class SortByFunctionArgumentsException extends RuntimeException {

    public SortByFunctionArgumentsException(String functionName, int argIndex, int expected, boolean null_) {
        super("Invalid argument index for function ["+functionName+"], expected ["+expected+"], supplied ["+argIndex+"]");
    }
    public SortByFunctionArgumentsException(String functionName, int argIndex, boolean null_) {
        super("Index ["+argIndex+"] conflicts with another argument using the same index for function ["+functionName+"]");
    }
    public SortByFunctionArgumentsException(String functionName, int argIndex) {
        super("Index ["+argIndex+"] out of bounds for function ["+functionName+"]");
    }
    public SortByFunctionArgumentsException(String functionName, int minRequiredArgs, int nArgs) {
        super("The function ["+functionName+"] requires ["+minRequiredArgs+"] arguments, ["+nArgs+"] were supplied.");
    }
}
