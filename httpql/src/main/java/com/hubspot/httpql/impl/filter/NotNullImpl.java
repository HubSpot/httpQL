package com.hubspot.httpql.impl.filter;

import com.google.common.collect.ImmutableSet;
import com.hubspot.httpql.ConditionProvider;
import com.hubspot.httpql.core.filter.Filter;
import com.hubspot.httpql.core.filter.NotNull;
import java.util.Set;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Param;

public class NotNullImpl extends FilterBase implements FilterImpl {

  @Override
  public <T> ConditionProvider<T> getConditionProvider(Field<T> field) {
    return new ConditionProvider<>(field) {
      @Override
      public Condition getCondition(Param<T> value) {
        return field.isNotNull();
      }
    };
  }

  @Override
  public Set<Class<? extends Filter>> getAnnotationClasses() {
    return ImmutableSet.of(NotNull.class, com.hubspot.httpql.filter.NotNull.class);
  }
}
