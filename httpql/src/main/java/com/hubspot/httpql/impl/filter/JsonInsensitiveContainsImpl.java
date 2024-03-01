package com.hubspot.httpql.impl.filter;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.escape.Escaper;
import com.google.common.escape.Escapers;
import com.hubspot.httpql.ConditionProvider;
import com.hubspot.httpql.MultiParamConditionProvider;
import com.hubspot.httpql.core.filter.Filter;
import com.hubspot.httpql.core.filter.JsonInsensitiveContains;
import java.util.Collection;
import java.util.Set;
import org.jooq.Condition;
import org.jooq.Field;

public class JsonInsensitiveContainsImpl extends JsonFilterBase implements FilterImpl {
  private static final Escaper ESCAPER = Escapers
    .builder()
    .addEscape('\\', "\\\\")
    .addEscape('%', "!%")
    .addEscape('_', "!_")
    .addEscape('!', "!!")
    .build();

  @SuppressWarnings("unchecked")
  @Override
  public <T> ConditionProvider<T> getConditionProvider(final Field<T> field) {
    return new MultiParamConditionProvider<T>(field) {

      @SuppressWarnings("unchecked")
      @Override
      public Condition getCondition(Collection<T> values) {
        Preconditions.checkArgument(
          values.size() == 2,
          "JsonInsensitiveContains filters require exactly 2 parameters"
        );
        JsonFilterParts jsonFilterParts = getJsonFilterParts(field, values);
        String escapedValue = ESCAPER.escape(jsonFilterParts.filterValues.get(0).data());
        return jsonFilterParts.fieldValue.likeIgnoreCase('%' + escapedValue + '%', '!');
      }
    };
  }

  @Override
  public Set<Class<? extends Filter>> getAnnotationClasses() {
    return ImmutableSet.of(
      JsonInsensitiveContains.class,
      com.hubspot.httpql.filter.JsonInsensitiveContains.class
    );
  }
}
