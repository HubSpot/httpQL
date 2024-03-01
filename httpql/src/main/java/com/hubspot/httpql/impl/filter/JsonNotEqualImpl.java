package com.hubspot.httpql.impl.filter;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.hubspot.httpql.ConditionProvider;
import com.hubspot.httpql.MultiParamConditionProvider;
import com.hubspot.httpql.core.filter.Filter;
import com.hubspot.httpql.core.filter.JsonNotEqual;
import java.util.Collection;
import java.util.Set;
import org.jooq.Condition;
import org.jooq.Field;

public class JsonNotEqualImpl extends JsonFilterBase implements FilterImpl {

  @SuppressWarnings("unchecked")
  @Override
  public <T> ConditionProvider<T> getConditionProvider(final Field<T> field) {
    return new MultiParamConditionProvider<T>(field) {

      @SuppressWarnings("unchecked")
      @Override
      public Condition getCondition(Collection<T> values) {
        Preconditions.checkArgument(
          values.size() == 2,
          "JsonNotEqual filters require exactly 2 parameters"
        );
        JsonFilterParts jsonFilterParts = getJsonFilterParts(field, values);
        return jsonFilterParts.fieldValue.notEqual(jsonFilterParts.filterValues.get(0));
      }
    };
  }

  @Override
  public Set<Class<? extends Filter>> getAnnotationClasses() {
    return ImmutableSet.of(
      JsonNotEqual.class,
      com.hubspot.httpql.filter.JsonNotEqual.class
    );
  }
}
