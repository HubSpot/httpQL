package com.hubspot.httpql.impl.filter;

import com.google.common.collect.ImmutableSet;
import com.hubspot.httpql.ConditionProvider;
import com.hubspot.httpql.core.filter.Filter;
import com.hubspot.httpql.core.filter.GreaterThan;
import java.util.Set;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Param;

public class GreaterThanImpl extends FilterBase implements FilterImpl {

  @Override
  public <T> ConditionProvider<T> getConditionProvider(final Field<T> field) {
    return new ConditionProvider<>(field) {
      @Override
      public Condition getCondition(Param<T> value) {
        return field.gt(value);
      }
    };
  }

  @Override
  public Set<Class<? extends Filter>> getAnnotationClasses() {
    return ImmutableSet.of(
      GreaterThan.class,
      com.hubspot.httpql.filter.GreaterThan.class
    );
  }
}
