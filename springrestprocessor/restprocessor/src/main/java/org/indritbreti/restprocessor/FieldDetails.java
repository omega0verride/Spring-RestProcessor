package org.indritbreti.restprocessor;

import org.indritbreti.restprocessor.DynamicQueryBuilder.DynamicSortBuilder.SortOrder;
import org.indritbreti.restprocessor.processor.TypeMirrorUtils;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class FieldDetails implements IRestFieldDetails, Cloneable, Serializable {
    public     static final String test_static_value = "static string";
    private String fieldName;
    private String apiName;
    private String uniqueAPIName;
    private final List<PersistencePath> persistencePaths = new ArrayList<>();
    private String persistencePath;
    private transient TypeElement type;
    private transient TypeElement filterClassType;
    private ElementKind elementKind;
    private String joinTable;

    private boolean required = false;
    private boolean useCustomApiNameFromAnnotation;
    private boolean sortable = true;
    private boolean filterable = true;

    private SortOrder defaultSortOrder = SortOrder.ASC;

    public FieldDetails() {
    }

    protected void setFieldName(String name) {
//        if (name == null || name.trim().length() == 0)
//            throw new DynamicQuerySortException("fieldName cannot be empty or null!");
        this.fieldName = name;
    }

    protected void setApiName(String apiName) {
        setApiName(apiName, false);
    }

    protected void setApiName(String apiName, boolean useCustomApiNameFromAnnotation) {
//        if (apiName == null || apiName.trim().length() == 0)
//            throw new DynamicQuerySortException("apiName cannot be empty or null!");
        if (useCustomApiNameFromAnnotation)
            this.useCustomApiNameFromAnnotation = true;
        this.apiName = apiName;
        this.uniqueAPIName = apiName;
    }

    public void setPersistencePaths(List<PersistencePath> persistencePaths) {
        if (persistencePaths != null) {
            this.persistencePaths.clear();
            this.persistencePaths.addAll(persistencePaths);
            buildPersistencePathCache();
        }
    }

    public void addPersistencePath(String persistencePath) {
        addPersistencePath(persistencePath, true);
    }

    public void addPersistencePath(String persistencePath, boolean useForUniqueName) {
        addPersistencePath(persistencePath, useForUniqueName, null);
    }

    public void addPersistencePath(String persistencePath, String joinTableClass) {
        addPersistencePath(persistencePath, true, joinTableClass);
    }

    public void addPersistencePath(String persistencePath, boolean useForUniqueName, String joinTableClass) {
        persistencePaths.add(new PersistencePath(persistencePath, useForUniqueName));
        if (joinTableClass != null)
            this.joinTable = joinTableClass;
        buildPersistencePathCache();
    }

    public void popPersistencePath() {
        persistencePaths.remove(persistencePaths.size() - 1);
        buildPersistencePathCache();
    }

    public void buildPersistencePathCache() {
        persistencePath = persistencePaths.stream().map(PersistencePath::getPath).reduce((s1, s2) -> s1 + "." + s2).orElse("");
    }

    public String getPersistencePathAsString() {
        return persistencePath;
    }

    public String buildFieldDetails(String path, TypeElement type, RESTField restFieldAnnotation, Hashtable<String, FieldDetails> sortableFields, ProcessingEnvironment processingEnvironment) {
        setFieldName(path);
        setApiName(path);
        if (restFieldAnnotation != null && restFieldAnnotation.persistenceName() != null && restFieldAnnotation.persistenceName().trim().length() != 0)
            addPersistencePath(restFieldAnnotation.persistenceName());
        else
            addPersistencePath(path);
        this.type = type;
        this.filterClassType = type;
        if (restFieldAnnotation != null) {
            TypeElement filterClassType_ = TypeMirrorUtils.getTypeElement(restFieldAnnotation, RESTField::filterClassType, processingEnvironment);
            if (filterClassType_ != null && !filterClassType_.getQualifiedName().toString().equals(Object.class.getCanonicalName()))
                this.filterClassType = filterClassType_;
            if(restFieldAnnotation.apiName() != null && restFieldAnnotation.apiName().trim().length() != 0)
                setApiName(restFieldAnnotation.apiName(), true);
            setRequired(restFieldAnnotation.required());
            this.filterable = restFieldAnnotation.filterable();
            this.sortable = restFieldAnnotation.sortable();
            this.defaultSortOrder = restFieldAnnotation.defaultSort();
        }
        this.elementKind = type.getKind();

        return evaluateUniqueAPIName(sortableFields);
    }

    @Override
    public FieldDetails clone() {
        FieldDetails fieldDetails = new FieldDetails();
        fieldDetails.setPersistencePaths(getPersistencePaths());
        fieldDetails.setFieldName(getFieldName());
        fieldDetails.setApiName(getApiName());
        fieldDetails.uniqueAPIName = uniqueAPIName;
        fieldDetails.joinTable = joinTable;
        fieldDetails.type = type;
        fieldDetails.required = required;
        fieldDetails.filterClassType = filterClassType;
        fieldDetails.useCustomApiNameFromAnnotation = useCustomApiNameFromAnnotation;
        fieldDetails.sortable = sortable;
        fieldDetails.filterable = filterable;
        fieldDetails.defaultSortOrder = defaultSortOrder;
        fieldDetails.persistencePath = persistencePath;
        fieldDetails.elementKind = getElementKind();
        fieldDetails.buildPersistencePathCache();
        return fieldDetails;
    }

    private String evaluateUniqueAPIName(Hashtable<String, FieldDetails> fieldDetails) {
        String apiName_ = getApiName();
        if (!useCustomApiNameFromAnnotation)
            apiName_ = persistencePaths.stream().filter(p -> p.useForUniqueName).map(PersistencePath::getPath).reduce(((p1, p2) -> p1 + "." + p2)).orElse(apiName);
        return evaluateUniqueAPIName(fieldDetails, apiName_, 0);
    }

    private String evaluateUniqueAPIName(Hashtable<String, FieldDetails> fieldDetails, String apiName_, int persistencePathsIndex) {
        if (fieldDetails.containsKey(apiName_)) // if somehow we end up with duplicate persistencePaths, add underscores until resolved
            return evaluateUniqueAPIName(fieldDetails, apiName_ + "_", persistencePathsIndex);
        this.uniqueAPIName = apiName_;
        return apiName_;
    }

//    private String evaluateUniqueAPINameOLD(Hashtable<String, FieldDetails> fieldDetails, String apiName_, int persistencePathsIndex) {
//        if (fieldDetails.containsKey(apiName_)) {
//            if (persistencePathsIndex < persistencePaths.size() - 1) {
//                List<String> parts = new ArrayList<>(persistencePaths.subList(0, persistencePathsIndex + 1));
//                parts.add(apiName_);
//                apiName_ = parts.stream().reduce(((p1, p2) -> p1 + "." + p2)).orElse(apiName_);
//                persistencePathsIndex++;
//            } else {
//                apiName_ = apiName_ + "_"; // if somehow we end up with duplicate persistencePaths, add underscores until resolved
//            }
//            return evaluateUniqueAPIName(fieldDetails, apiName_, persistencePathsIndex);
//        }
//        this.uniqueAPIName = apiName_;
//        return apiName_;
//    }


    public boolean isJoin() {
        return joinTable != null;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getApiName() {
        return apiName;
    }

    public String getUniqueAPIName() {
        return uniqueAPIName;
    }

    public String getUniqueAPINameUnderscore() {
        return uniqueAPIName.replaceAll("\\.", "_");
    }

    public List<PersistencePath> getPersistencePaths() {
        return new ArrayList<>(persistencePaths);
    }

    public String getPersistencePath() {
        return persistencePath;
    }

    public String getJoinTable() {
        return joinTable;
    }

    public TypeElement getType() {
        return type;
    }

    private void setRequired(boolean b) {
        this.required = b;
    }

    public boolean getRequired() {
        return required;
    }

    public boolean isRequired() {
        return required;
    }

    public TypeElement getFilterClassType() {
        return filterClassType;
    }

    public void setFilterClassType(TypeElement filterClassType) {
        this.filterClassType = filterClassType;
    }

    public boolean isSortable() {
        return sortable;
    }

    public boolean isFilterable() {
        return filterable;
    }

    public SortOrder getDefaultSortOrder() {
        return defaultSortOrder;
    }

    public ElementKind getElementKind() {
        return elementKind;
    }
}
