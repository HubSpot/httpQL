package com.hubspot.httpql.ann;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({
    ElementType.FIELD, ElementType.METHOD
})
public @interface FilterJoin {
  String table();

  String on();

  String eq();
}
