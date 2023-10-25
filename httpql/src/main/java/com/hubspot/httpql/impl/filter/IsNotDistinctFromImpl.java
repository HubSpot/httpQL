package com.hubspot.httpql.impl.filter;

import com.google.common.collect.ImmutableSet;
import com.hubspot.httpql.ConditionProvider;
import com.hubspot.httpql.core.filter.FilterIF;
import com.hubspot.httpql.core.filter.IsNotDistinctFrom;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Param;

import java.util.Set;

public class IsNotDistinctFromImpl extends FilterBase implements FilterImpl {

  @Override
  public String[] names() {
    return new String[] {
        "ndistinct"
    };
  }

  @Override
  public <T> ConditionProvider<T> getConditionProvider(final Field<T> field) {
    return new ConditionProvider<T>(field) {

      @Override
      public Condition getCondition(Param<T> value) {
        return field.isNotDistinctFrom(value);
      }
    };
  }

  @Override
  public Set<Class<? extends FilterIF>> getAnnotationClasses() {
    return ImmutableSet.of(IsNotDistinctFrom.class, com.hubspot.httpql.filter.IsNotDistinctFrom.class);
  }
}
