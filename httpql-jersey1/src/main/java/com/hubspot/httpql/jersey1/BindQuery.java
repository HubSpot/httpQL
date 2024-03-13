package com.hubspot.httpql.jersey1;

import com.hubspot.httpql.QuerySpec;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface BindQuery {
  Class<? extends QuerySpec> value();
}
