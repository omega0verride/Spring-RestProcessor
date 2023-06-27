package org.indritbreti.restprocessor;

import org.indritbreti.restprocessor.DynamicQueryBuilder.DynamicSortBuilder.SortOrder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RESTField {
    String apiName() default "";

    String persistenceName() default "";
    boolean required() default false;

    Class<?> filterClassType()  default Object.class;

    boolean sortable() default true;
    boolean filterable() default true;

    SortOrder defaultSort() default SortOrder.ASC;
}
