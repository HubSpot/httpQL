package com.hubspot.httpql.impl.filter;

import com.google.common.collect.ImmutableSet;
import com.hubspot.httpql.ConditionProvider;
import com.hubspot.httpql.MultiParamConditionProvider;
import com.hubspot.httpql.core.filter.Filter;
import com.hubspot.httpql.core.filter.NotIn;
import org.jooq.Condition;
import org.jooq.Field;

import java.util.Collection;
import java.util.Set;

public class NotInImpl extends FilterBase implements FilterImpl {

  @Override
  public <T> ConditionProvider<T> getConditionProvider(final Field<T> field) {
    return new MultiParamConditionProvider<>(field) {

      @Override
      public Condition getCondition(Collection<T> values) {
        return field.notIn(values);
      }

    };
  }

  @Override
  public Set<Class<? extends Filter>> getAnnotationClasses() {
    return ImmutableSet.of(NotIn.class, com.hubspot.httpql.filter.NotIn.class);
  }

}
