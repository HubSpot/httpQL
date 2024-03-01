package com.hubspot.httpql.impl.filter;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.hubspot.httpql.ConditionProvider;
import com.hubspot.httpql.MultiParamConditionProvider;
import com.hubspot.httpql.core.filter.Filter;
import com.hubspot.httpql.core.filter.JsonRange;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.JSON;

public class JsonRangeImpl extends JsonFilterBase implements FilterImpl {

  @SuppressWarnings("unchecked")
  @Override
  public <T> ConditionProvider<T> getConditionProvider(final Field<T> field) {
    final Comparator<JSON> NUMBER_COMPARATOR = Comparator.comparing(
      a -> new BigDecimal(a.data())
    );

    return new MultiParamConditionProvider<T>(field) {

      @SuppressWarnings("unchecked")
      @Override
      public Condition getCondition(Collection<T> values) {
        Preconditions.checkArgument(
          values.size() == 3,
          "JsonRange filters require exactly 3 parameters"
        );
        JsonFilterParts jsonFilterParts = getJsonFilterParts(field, values);

        jsonFilterParts.filterValues.sort(NUMBER_COMPARATOR);
        return jsonFilterParts
          .fieldValue.cast(BigDecimal.class)
          .between(
            new BigDecimal(jsonFilterParts.filterValues.get(0).data()),
            new BigDecimal(jsonFilterParts.filterValues.get(1).data())
          );
      }
    };
  }

  @Override
  public Set<Class<? extends Filter>> getAnnotationClasses() {
    return ImmutableSet.of(JsonRange.class, com.hubspot.httpql.filter.JsonRange.class);
  }
}
