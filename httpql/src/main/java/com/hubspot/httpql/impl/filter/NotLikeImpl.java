package com.hubspot.httpql.impl.filter;

import com.google.common.collect.ImmutableSet;
import com.hubspot.httpql.ConditionProvider;
import com.hubspot.httpql.MultiParamConditionProvider;
import com.hubspot.httpql.core.filter.Filter;
import com.hubspot.httpql.core.filter.NotLike;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import org.jooq.Condition;
import org.jooq.Field;

public class NotLikeImpl extends FilterBase implements FilterImpl {

  @Override
  public <T> ConditionProvider<T> getConditionProvider(final Field<T> field) {
    return new MultiParamConditionProvider<>(field) {
      @Override
      public Condition getCondition(Collection<T> values) {
        Iterator<T> iter = values.iterator();
        Condition notLikeCondition = field.notLike((String) iter.next(), '!');
        while (iter.hasNext()) {
          notLikeCondition =
            notLikeCondition.and(field.notLike((String) iter.next(), '!'));
        }
        return notLikeCondition;
      }
    };
  }

  @Override
  public Set<Class<? extends Filter>> getAnnotationClasses() {
    return ImmutableSet.of(NotLike.class, com.hubspot.httpql.filter.NotLike.class);
  }
}
