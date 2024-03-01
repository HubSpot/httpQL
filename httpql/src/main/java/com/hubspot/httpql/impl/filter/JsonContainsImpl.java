package com.hubspot.httpql.impl.filter;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.hubspot.httpql.MultiParamConditionProvider;
import com.hubspot.httpql.core.filter.Filter;
import com.hubspot.httpql.core.filter.JsonContains;
import java.util.Collection;
import java.util.Set;
import org.jooq.Condition;
import org.jooq.Field;

public class JsonContainsImpl extends JsonFilterBase implements FilterImpl {

  @Override
  public <T> MultiParamConditionProvider<T> getConditionProvider(final Field<T> field) {
    return new MultiParamConditionProvider<>(field) {

      @Override
      public Condition getCondition(Collection<T> values) {
        Preconditions.checkArgument(
          values.size() == 2,
          "JsonContains filters require exactly 2 parameters"
        );
        JsonFilterParts jsonFilterParts = getJsonFilterParts(field, values);
        return jsonFilterParts.fieldValue.contains(jsonFilterParts.filterValues.get(0));
      }
    };
  }

  @Override
  public Set<Class<? extends Filter>> getAnnotationClasses() {
    return ImmutableSet.of(
      JsonContains.class,
      com.hubspot.httpql.filter.JsonContains.class
    );
  }
}
