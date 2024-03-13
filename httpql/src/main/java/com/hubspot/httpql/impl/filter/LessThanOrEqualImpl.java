package com.hubspot.httpql.impl.filter;

import com.google.common.collect.ImmutableSet;
import com.hubspot.httpql.ConditionProvider;
import com.hubspot.httpql.core.filter.Filter;
import com.hubspot.httpql.core.filter.LessThanOrEqual;
import java.util.Set;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Param;

public class LessThanOrEqualImpl extends FilterBase implements FilterImpl {

  @Override
  public <T> ConditionProvider<T> getConditionProvider(final Field<T> field) {
    return new ConditionProvider<>(field) {
      @Override
      public Condition getCondition(Param<T> value) {
        return field.lessOrEqual(value);
      }
    };
  }

  @Override
  public Set<Class<? extends Filter>> getAnnotationClasses() {
    return ImmutableSet.of(
      LessThanOrEqual.class,
      com.hubspot.httpql.filter.LessThanOrEqual.class
    );
  }
}
