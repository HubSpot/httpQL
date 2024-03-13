package com.hubspot.httpql.core.ann;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for QuerySpec providers; defines constraints around query size and seek depth.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface QueryConstraints {
  int defaultLimit();

  int maxLimit();

  int maxOffset();
}
