package com.hubspot.httpql.impl.filter;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.hubspot.httpql.ConditionProvider;
import com.hubspot.httpql.MultiParamConditionProvider;
import com.hubspot.httpql.core.filter.Filter;
import com.hubspot.httpql.core.filter.JsonIsDistinctFrom;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.JSON;
import org.jooq.impl.DSL;

public class JsonIsDistinctFromImpl extends JsonFilterBase implements FilterImpl {

  @SuppressWarnings("unchecked")
  @Override
  public <T> ConditionProvider<T> getConditionProvider(final Field<T> field) {
    return new MultiParamConditionProvider<T>(field) {

      @SuppressWarnings("unchecked")
      @Override
      public Condition getCondition(Collection<T> values) {
        Preconditions.checkArgument(
          values.size() >= 2,
          "JsonIsDistinctFrom filters require at least 2 parameters"
        );
        JsonFilterParts jsonFilterParts = getJsonFilterParts(field, values);

        return DSL.and(
          values
            .stream()
            .skip(1)
            .map(
              value ->
                jsonFilterParts.fieldValue.isDistinctFrom(JSON.json(value.toString()))
            )
            .collect(Collectors.toList())
        );
      }
    };
  }

  @Override
  public Set<Class<? extends Filter>> getAnnotationClasses() {
    return ImmutableSet.of(
      JsonIsDistinctFrom.class,
      com.hubspot.httpql.filter.JsonIsDistinctFrom.class
    );
  }
}
