package com.hubspot.httpql.impl.filter;

import com.hubspot.httpql.ConditionProvider;
import com.hubspot.httpql.MultiParamConditionProvider;
import com.hubspot.httpql.core.filter.FilterIF;
import com.hubspot.httpql.core.filter.IsDistinctFrom;
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
  public Class<? extends FilterIF> getAnnotationClass() {
    return IsDistinctFrom.class;
  }
}
