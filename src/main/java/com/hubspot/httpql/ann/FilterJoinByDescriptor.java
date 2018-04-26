package com.hubspot.httpql.ann;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.hubspot.httpql.ann.desc.JoinDescriptor;

@Retention(RetentionPolicy.RUNTIME)
@Target({
    ElementType.FIELD, ElementType.METHOD
})
public @interface FilterJoinByDescriptor {
  Class<? extends JoinDescriptor> value();
}
