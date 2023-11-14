package com.hubspot.httpql.ann;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @deprecated Use #{@link com.hubspot.httpql.core.ann.FilterJoin}
 */
@Deprecated
@Retention(RetentionPolicy.RUNTIME)
@Target({
    ElementType.FIELD, ElementType.METHOD
})
public @interface FilterJoin {
  String table();

  String on();

  String eq();
}
