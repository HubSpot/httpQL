package com.hubspot.httpql.core.ann;

import com.hubspot.httpql.core.filter.FilterIF;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({
    ElementType.FIELD, ElementType.METHOD
})
public @interface FilterBy {
  Class<? extends FilterIF>[] value();

  String as() default "";

  Class<?> typeOverride() default void.class;
}
