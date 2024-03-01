package com.hubspot.httpql.impl.filter;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.hubspot.httpql.MultiParamConditionProvider;
import com.hubspot.httpql.core.filter.Filter;
import com.hubspot.httpql.core.filter.JsonStartsWith;
import java.util.Collection;
import java.util.Set;
import org.jooq.Condition;
import org.jooq.Field;

public class JsonStartsWithImpl extends JsonFilterBase implements FilterImpl {

  @SuppressWarnings("unchecked")
  @Override
  public <T> MultiParamConditionProvider<T> getConditionProvider(final Field<T> field) {
    return new MultiParamConditionProvider<T>(field) {

      @Override
      public Condition getCondition(Collection<T> values) {
        Preconditions.checkArgument(
          values.size() == 2,
          "JsonStartsWith filters require exactly 2 parameters"
        );
        JsonFilterParts jsonFilterParts = getJsonFilterParts(field, values);
        return jsonFilterParts
          .fieldValue.cast(String.class)
          .startsWith(jsonFilterParts.filterValues.get(0).data());
      }
    };
  }

  @Override
  public Set<Class<? extends Filter>> getAnnotationClasses() {
    return ImmutableSet.of(
      JsonStartsWith.class,
      com.hubspot.httpql.filter.JsonStartsWith.class
    );
  }
}
