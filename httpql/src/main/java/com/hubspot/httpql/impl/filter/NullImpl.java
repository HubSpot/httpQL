package com.hubspot.httpql.impl.filter;

import com.google.common.collect.ImmutableSet;
import com.hubspot.httpql.ConditionProvider;
import com.hubspot.httpql.core.filter.FilterIF;
import com.hubspot.httpql.core.filter.Null;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Param;

import java.util.Set;

public class NullImpl extends FilterBase implements FilterImpl {
  @Override
  public String[] names() {
    return new String[] {
        "is_null"
    };
  }

  @Override
  public <T> ConditionProvider<T> getConditionProvider(Field<T> field) {
    return new ConditionProvider<T>(field) {

      @Override
      public Condition getCondition(Param<T> value) {
        return field.isNull();
      }
    };
  }

  @Override
  public Set<Class<? extends FilterIF>> getAnnotationClasses() {
    return ImmutableSet.of(Null.class, com.hubspot.httpql.filter.Null.class);
  }
}
