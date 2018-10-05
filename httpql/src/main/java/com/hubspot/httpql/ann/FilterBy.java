package com.hubspot.httpql.ann;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.hubspot.httpql.Filter;
import com.hubspot.httpql.MultiParamConditionProvider;

/**
 * Annotate a field to indicate it may be filtered/queried.
 *
 * @param value
 *          One or more @ Filter} (operators) to permit
 * @param as
 *          Real name of the column/field, if we're annotating something for a {@link MultiParamConditionProvider}
 * @param typeOverride
 *          Specifies that the type of the filter value is different from the type of the annotated field or method
 * @author tdavis
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({
    ElementType.FIELD, ElementType.METHOD
})
public @interface FilterBy {
  Class<? extends Filter>[] value();

  String as() default "";

  Class<?> typeOverride() default void.class;
}
