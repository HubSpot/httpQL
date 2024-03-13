package com.hubspot.httpql;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @deprecated Use #{@link com.hubspot.httpql.core.ann.QueryConstraints}
 */
@Deprecated
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface QueryConstraints {
  int defaultLimit();

  int maxLimit();

  int maxOffset();
}
