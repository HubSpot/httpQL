package com.hubspot.httpql.impl.filter;

import com.google.common.collect.ImmutableSet;
import com.hubspot.httpql.ConditionProvider;
import com.hubspot.httpql.core.filter.Filter;
import com.hubspot.httpql.core.filter.JsonNull;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Param;

public class JsonNullImpl extends JsonFilterBase implements FilterImpl {

  @Override
  public <T> ConditionProvider<T> getConditionProvider(final Field<T> field) {
    return new ConditionProvider<>(field) {

      @Override
      public Condition getCondition(Param<T> value) {
        JsonFilterParts jsonFilterParts = getJsonFilterParts(
          field,
          List.of(Objects.requireNonNull(value.getValue()))
        );
        return jsonFilterParts.fieldValue.isNull();
      }
    };
  }

  @Override
  public Set<Class<? extends Filter>> getAnnotationClasses() {
    return ImmutableSet.of(JsonNull.class, com.hubspot.httpql.filter.JsonNull.class);
  }
}
