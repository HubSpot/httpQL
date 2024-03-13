package com.hubspot.httpql.ann;

import com.hubspot.httpql.Filter;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @deprecated Use #{@link com.hubspot.httpql.core.ann.FilterBy}
 */
@Deprecated
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface FilterBy {
  Class<? extends Filter>[] value();

  String as() default "";

  Class<?> typeOverride() default void.class;
}
