package org.indritbreti.restprocessor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DynamicRestMapping {
    String[] path() default {};
    RequestMethod requestMethod();

    Class<?> entity();
}
