package org.indritbreti.restprocessor;

import java.io.Serializable;

// this class is used to keep track of the fields declared as RESTField
// useForUniqueName will decide if the path is required to build the uniqueApiName
public class PersistencePath implements Serializable {
    String path;
    boolean useForUniqueName;

    public PersistencePath(String path) {
        this(path, true);
    }

    public PersistencePath(String path, boolean useForUniqueName) {
        this.path = path;
        this.useForUniqueName = useForUniqueName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isUseForUniqueName() {
        return useForUniqueName;
    }
    public boolean getUseForUniqueName() {
        return useForUniqueName;
    }

    public void setUseForUniqueName(boolean useForUniqueName) {
        this.useForUniqueName = useForUniqueName;
    }
}
