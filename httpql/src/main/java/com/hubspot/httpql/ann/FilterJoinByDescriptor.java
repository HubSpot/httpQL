package com.hubspot.httpql.ann;

import com.hubspot.httpql.ann.desc.JoinDescriptor;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @deprecated Use #{@link com.hubspot.httpql.core.ann.FilterJoinByDescriptor}
 */
@Deprecated
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface FilterJoinByDescriptor {
  Class<? extends JoinDescriptor> value();
}
