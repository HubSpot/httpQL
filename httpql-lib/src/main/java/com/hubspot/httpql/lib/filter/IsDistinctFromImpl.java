package com.hubspot.httpql.lib.filter;

import com.hubspot.httpql.core.filter.Filter;
import com.hubspot.httpql.core.filter.IsDistinctFrom;
import com.hubspot.httpql.lib.ConditionProvider;
import com.hubspot.httpql.lib.MultiParamConditionProvider;
import java.util.Collection;
import java.util.stream.Collectors;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.impl.DSL;

public class IsDistinctFromImpl extends FilterBase implements FilterImpl {

  @Override
  public String[] names() {
    return new String[] {
        "distinct"
    };
  }

  @Override
  public <T> ConditionProvider<T> getConditionProvider(final Field<T> field) {
    return new MultiParamConditionProvider<T>(field) {

      @Override
      public Condition getCondition(Collection<T> values) {
        return DSL.and(values.stream()
            .map(field::isDistinctFrom)
            .collect(Collectors.toList()));
      }
    };
  }

  @Override
  public Class<? extends Filter> getAnnotationClass() {
    return IsDistinctFrom.class;
  }
}
