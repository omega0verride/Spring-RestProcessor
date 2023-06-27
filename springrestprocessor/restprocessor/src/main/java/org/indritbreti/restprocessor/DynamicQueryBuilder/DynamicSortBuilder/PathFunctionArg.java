package org.indritbreti.restprocessor.DynamicQueryBuilder.DynamicSortBuilder;

import java.util.ArrayList;
import java.util.List;

public class PathFunctionArg extends FunctionArg {
    List<String> persistencePaths;

    public PathFunctionArg(int index, String ... persistencePaths) {
        super(index);
        if (persistencePaths == null || persistencePaths.length == 0)
            throw new IllegalArgumentException("persistencePaths must have more than [0] elements");
        this.persistencePaths = List.of(persistencePaths);
    }

    public List<String> getPaths() {
        return new ArrayList<>(persistencePaths);
    }
}
