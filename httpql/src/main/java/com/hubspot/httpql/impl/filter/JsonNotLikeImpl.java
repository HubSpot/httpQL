package com.hubspot.httpql.impl.filter;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.hubspot.httpql.ConditionProvider;
import com.hubspot.httpql.MultiParamConditionProvider;
import com.hubspot.httpql.core.filter.Filter;
import com.hubspot.httpql.core.filter.JsonNotLike;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import org.jooq.Condition;
import org.jooq.Field;

public class JsonNotLikeImpl extends JsonFilterBase implements FilterImpl {

  @SuppressWarnings("unchecked")
  @Override
  public <T> ConditionProvider<T> getConditionProvider(final Field<T> field) {
    return new MultiParamConditionProvider<T>(field) {

      @SuppressWarnings("unchecked")
      @Override
      public Condition getCondition(Collection<T> values) {
        Preconditions.checkArgument(
          values.size() >= 2,
          "JsonNotLike filters require at least 2 parameters"
        );
        JsonFilterParts jsonFilterParts = getJsonFilterParts(field, values);

        Iterator<String> iter = values.stream().skip(1).map(Object::toString).iterator();
        Condition notLikeCondition = jsonFilterParts
          .fieldValue.cast(String.class)
          .notLike(iter.next(), '!');
        while (iter.hasNext()) {
          notLikeCondition =
            notLikeCondition.and(
              jsonFilterParts.fieldValue.cast(String.class).notLike(iter.next(), '!')
            );
        }
        return notLikeCondition;
      }
    };
  }

  @Override
  public Set<Class<? extends Filter>> getAnnotationClasses() {
    return ImmutableSet.of(
      JsonNotLike.class,
      com.hubspot.httpql.filter.JsonNotLike.class
    );
  }
}
