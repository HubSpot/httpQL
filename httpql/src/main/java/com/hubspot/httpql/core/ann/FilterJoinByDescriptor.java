package com.hubspot.httpql.core.ann;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

;

/**
 * TODO: implement
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({
    ElementType.FIELD, ElementType.METHOD
})
public @interface FilterJoinByDescriptor {
  /**
   * The full class name of the filter implementation like com.hubspot.lib.data.MyFilterDescriptor
   */
  String value();
}
